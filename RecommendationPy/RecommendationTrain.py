# train_hybrid_kafka_optimized.py
import os
os.environ.setdefault("OPENBLAS_NUM_THREADS", "1")

import json, time, numpy as np, pandas as pd, re
from sqlalchemy import create_engine
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.preprocessing import normalize
import redis, joblib
from tqdm import tqdm
from collections import defaultdict
import unicodedata
from sklearn.metrics.pairwise import linear_kernel

# optional ALS
try:
    import implicit
    IMPLICIT = True
except:
    IMPLICIT = False

from kafka import KafkaProducer

# ---------------------------
# Config (tune these)
# ---------------------------
REDIS_PORT = int(os.getenv("REDIS_PORT", 6379))
MYSQL_URI = os.getenv("MYSQL_URI", "mysql+pymysql://root:715826@localhost:3306/internlink")
REDIS_HOST = os.getenv("REDIS_HOST", "localhost")
KAFKA_BOOTSTRAP = os.getenv("KAFKA_BOOTSTRAP", "localhost:29092")
KAFKA_TOPIC = os.getenv("KAFKA_TOPIC", "student_recommendation")

TOP_K = int(os.getenv("TOP_K", 20))
ALPHA = float(os.getenv("ALPHA", 0.4))  # content weight
BETA = float(os.getenv("BETA", 0.6))    # behavior weight
TFIDF_MODEL_PATH = os.getenv("TFIDF_MODEL_PATH", "models/tfidf_vec.joblib")

# Adjusted event weights (VIEW/CLICK up a bit, SAVE/APPLY important)
EVENT_WEIGHTS = {'VIEW': 0.5, 'CLICK': 1.0, 'SAVE': 3.0, 'APPLY': 8.0}

# ALS tuning defaults (we will try reasonable configs)
ALS_FACTORS_TRY = [32, 64]
ALS_REG_TRY = [0.01, 0.08, 0.001]
ALS_ITERS_TRY = [20, 40, 50]

# Candidate pool size from content before hybrid re-ranking
CONTENT_POOL_N = 200

# Faculty / skill boost multipliers (quick wins)
FACULTY_BOOST_SCALE = float(os.getenv("FACULTY_BOOST_SCALE", 0.60))
SKILL_BOOST_WEIGHT = float(os.getenv("SKILL_BOOST_WEIGHT", 0.3))

# Recency half-life (days) for recency weighting
RECENCY_HALF_LIFE_DAYS = float(os.getenv("RECENCY_HALF_LIFE_DAYS", 90.0))

# Time-based split ratio (fraction of latest interactions per user used for test)
TIME_SPLIT_RATIO = float(os.getenv("TIME_SPLIT_RATIO", 0.25))

# ---------------------------
# Connections
# ---------------------------
engine = create_engine(MYSQL_URI, pool_pre_ping=True)
r = redis.Redis(host=REDIS_HOST, port=REDIS_PORT, db=0, decode_responses=True)
producer = KafkaProducer(
    bootstrap_servers=KAFKA_BOOTSTRAP,
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

# ---------------------------
# Load data
# ---------------------------
def load_data():
    with engine.connect() as conn:
        # jobdetail
        jobs = pd.read_sql("SELECT id,title,description,working_address,field_id FROM jobdetail", con=conn)

        # students: faculty via organization_faculty & concat skills
        students = pd.read_sql("""
            SELECT s.id AS student_id,
                   ofa.faculty_id AS faculty_id,
                   COALESCE(GROUP_CONCAT(sk.skill_name SEPARATOR ' '), '') AS skills
            FROM student s
            LEFT JOIN organization_faculty ofa ON s.organization_faculty_id = ofa.id
            LEFT JOIN studentskill ss ON s.id = ss.student_id
            LEFT JOIN skill sk ON ss.skill_id = sk.id
            GROUP BY s.id, ofa.faculty_id
        """, con=conn)

        # interactions: try to read both 'timestamp' or 'created_at' - we'll select both to be safe
        interactions = pd.read_sql("SELECT student_id, job_id, behavior_type, COALESCE(`timestamp`, created_at) AS event_time FROM student_behavior", con=conn)

    jobs = jobs.fillna({'title':'','description':'','working_address':''})
    jobs['field_id'] = jobs['field_id'].astype('Int64')

    students['skills'] = students['skills'].fillna('')
    students['faculty_id'] = students['faculty_id'].astype('Int64')

    interactions['behavior_type'] = interactions['behavior_type'].astype(str).str.upper()
    interactions['weight'] = interactions['behavior_type'].map(EVENT_WEIGHTS).fillna(1.0)
    # normalize event_time -> created_at column name expected in other functions
    interactions = interactions.rename(columns={'event_time': 'created_at'})
    interactions['created_at'] = pd.to_datetime(interactions['created_at'], errors='coerce')
    return jobs, students, interactions

# ---------------------------
# TF-IDF content
# ---------------------------
def normalize_text(s):
    s = str(s).lower()
    s = re.sub(r'[^\w\s]', ' ', s, flags=re.UNICODE)
    s = re.sub(r'\s+', ' ', s).strip()
    return s

def build_tfidf(jobs, students):
    jobs['doc'] = (jobs['title'].fillna('') + ' ' + jobs['description'].fillna('') + ' ' + jobs['working_address'].fillna('')).map(normalize_text)
    def student_doc(row):
        skills = row['skills'] or ''
        faculty = ''
        if pd.notna(row.get('faculty_id')) and str(row['faculty_id']).strip() != '':
            faculty = f' faculty_{int(row["faculty_id"])}'
        return normalize_text(str(skills) + faculty)
    students['doc'] = students.apply(student_doc, axis=1)

    corpus = pd.concat([jobs['doc'], students['doc']])
    # tuned TF-IDF: keep ngrams, allow low min_df for small corpora
    vec = TfidfVectorizer(max_features=15000, ngram_range=(1,2), min_df=1, sublinear_tf=True, norm='l2')
    print("Fitting TF-IDF...")
    vec.fit(corpus.values)
    job_vec = vec.transform(jobs['doc'].values)
    student_vec = vec.transform(students['doc'].values)
    job_vec = normalize(job_vec, axis=1, norm='l2', copy=False)
    student_vec = normalize(student_vec, axis=1, norm='l2', copy=False)
    os.makedirs('models', exist_ok=True)
    joblib.dump(vec, TFIDF_MODEL_PATH)
    return job_vec, student_vec, vec

# ---------------------------
# Popularity fallback
# ---------------------------
def compute_popularity(interactions, jobs):
    pop = interactions.groupby('job_id').size().sort_values(ascending=False)
    pop_map = [(int(jid), int(cnt)) for jid, cnt in pop.items()]

    job_field = jobs.set_index('id')['field_id'].to_dict()
    rows = []
    for _, row in interactions.iterrows():
        jid = int(row['job_id'])
        fid = job_field.get(jid, None)
        if pd.notna(fid):
            rows.append((int(fid), jid))
    if rows:
        df = pd.DataFrame(rows, columns=['field_id','job_id'])
        pop_by_field = df.groupby(['field_id','job_id']).size().reset_index(name='count')
        pop_by_field_map = {}
        for fid, grp in pop_by_field.groupby('field_id'):
            pop_by_field_map[int(fid)] = [
                (int(r['job_id']), int(r['count']))
                for _, r in grp.sort_values('count', ascending=False).iterrows()
            ]
    else:
        pop_by_field_map = {}

    return pop_map, pop_by_field_map

# ---------------------------
# Content-based top-K with skill boost (candidate pool)
# ---------------------------
def load_skill_list(conn):
    df = pd.read_sql("SELECT id, skill_name FROM skill", con=conn)
    df['skill_norm'] = df['skill_name'].astype(str).map(normalize_text)
    skill_set = set(df['skill_norm'].tolist())
    skill_map = {row['skill_norm']: row['skill_name'] for _, row in df.iterrows()}
    return skill_set, skill_map

def compute_content_topk(jobs, students, job_vec, student_vec, top_k=TOP_K, pop_map=None, pop_by_field_map=None, vec=None):
    job_ids = jobs['id'].to_numpy()
    job_field_ids = jobs['field_id'].to_numpy()
    results = {}

    job_term_sets = [set(row['doc'].split()) for _, row in jobs.iterrows()]
    student_term_sets = [set(row['doc'].split()) for _, row in students.iterrows()]

    with engine.connect() as conn:
        skill_set, skill_map = load_skill_list(conn)

    vocab = vec.vocabulary_
    idf = getattr(vec, "idf_", None)

    for i in tqdm(range(len(students))):
        student_id = int(students.iloc[i]['student_id'])
        faculty_id = students.iloc[i]['faculty_id']
        s_vec = student_vec[i]
        sims = linear_kernel(s_vec, job_vec).ravel()

        # skill exact match boost
        student_terms = student_term_sets[i]
        boost = np.zeros_like(sims)
        if student_terms:
            denom = 0.0
            if idf is not None:
                term_idfs = [idf[vocab[t]] for t in student_terms if t in vocab]
                denom = sum(term_idfs) + 1e-9
            else:
                denom = len(student_terms) + 1e-9

            for j in range(len(jobs)):
                inter = student_terms & job_term_sets[j]
                if inter:
                    if idf is not None:
                        boost_val = sum(idf[vocab[t]] for t in inter if t in vocab) / denom
                    else:
                        boost_val = len(inter) / denom
                    boost[j] = boost_val

        # faculty soft-boost (bigger)
        faculty_boost_mask = (job_field_ids == int(faculty_id)) if pd.notna(faculty_id) else np.zeros_like(sims, dtype=bool)
        if faculty_boost_mask.any():
            sims = sims + (faculty_boost_mask.astype(float) * FACULTY_BOOST_SCALE * (sims.max() if sims.size else 1.0))

        # combine (skill weight increased)
        row = sims + boost * SKILL_BOOST_WEIGHT

        # candidate pool (top N content candidates)
        pool_n = max(top_k, min(CONTENT_POOL_N, len(row)))
        if pool_n <= 0:
            content_list = []
        else:
            top_idx = np.argpartition(-row, min(pool_n, len(row)-1))[:pool_n]
            top_idx = top_idx[np.argsort(-row[top_idx])]
            content_list = [(int(job_ids[j]), float(row[j])) for j in top_idx if row[j] > 0.0]

        # fallback popularity if not enough
        if len(content_list) < top_k:
            seen = {jid for jid, _ in content_list}
            needed = top_k - len(content_list)
            added = []
            if pd.notna(faculty_id) and pop_by_field_map and int(faculty_id) in pop_by_field_map:
                for jid, cnt in pop_by_field_map[int(faculty_id)]:
                    if jid not in seen:
                        added.append((int(jid), float(cnt)))
                        seen.add(jid)
                    if len(added) >= needed:
                        break
            if pop_map and len(added) < needed:
                for jid, cnt in pop_map:
                    if jid not in seen:
                        added.append((int(jid), float(cnt)))
                        seen.add(jid)
                    if len(added) >= needed:
                        break
            content_list.extend(added[:needed])

        results[student_id] = content_list[:top_k]

    return results

# ---------------------------
# ALS collaborative with small-grid training
# ---------------------------
def build_interaction_matrix(interactions, students, jobs, recency_half_life_days=RECENCY_HALF_LIFE_DAYS):
    if interactions.empty:
        user_ids = sorted(students['student_id'].unique())
        item_ids = sorted(jobs['id'].unique())
        from scipy.sparse import csr_matrix
        return {u:i for i,u in enumerate(user_ids)}, {j:i for i,j in enumerate(item_ids)}, csr_matrix((len(user_ids), len(item_ids))), user_ids, item_ids

    inter = interactions.copy()
    now = inter['created_at'].max() if not inter['created_at'].isna().all() else pd.Timestamp.utcnow()

    def recency_factor(ts):
        days = (now - pd.to_datetime(ts)).days
        return 0.5 ** (days / recency_half_life_days)

    inter['recency_factor'] = inter['created_at'].apply(recency_factor)
    inter['w'] = inter['weight'] * (1.0 + inter['recency_factor'])

    agg = inter.groupby(['student_id','job_id'], as_index=False)['w'].sum().rename(columns={'w':'weight'})
    user_ids = sorted(students['student_id'].unique())
    item_ids = sorted(jobs['id'].unique())
    user_idx = {u:i for i,u in enumerate(user_ids)}
    item_idx = {j:i for i,j in enumerate(item_ids)}
    agg = agg[agg['student_id'].isin(user_idx.keys()) & agg['job_id'].isin(item_idx.keys())]

    from scipy.sparse import csr_matrix
    rows = agg['student_id'].map(user_idx).to_numpy()
    cols = agg['job_id'].map(item_idx).to_numpy()
    confidence = 1.0 + 10.0 * agg['weight'].to_numpy(dtype=float)
    mat = csr_matrix((confidence, (rows, cols)), shape=(len(user_ids), len(item_ids)))
    return user_idx, item_idx, mat, user_ids, item_ids

def train_als(user_idx, item_idx, mat, user_ids, item_ids, top_k=TOP_K,
              try_factors=ALS_FACTORS_TRY, try_reg=ALS_REG_TRY, try_iters=ALS_ITERS_TRY):
    if not IMPLICIT or mat.nnz==0:
        return {}
    print("Training ALS (trying small grid)...")
    # Here we actually try the grid but use a stronger default model for each try
    for f in try_factors:
        for reg in try_reg:
            for it in try_iters:
                try:
                    model = implicit.als.AlternatingLeastSquares(
                        factors=f,
                        regularization=reg,
                        iterations=it,
                        use_cg=True,
                        random_state=42
                    )
                    model.fit((mat.T * 1.0).astype('double'))
                    user_recs = {}
                    for uid in tqdm(user_ids, leave=False):
                        idx = user_idx.get(uid)
                        if idx is None:
                            user_recs[uid] = []
                            continue
                        try:
                            recs = model.recommend(idx, mat, N=top_k, filter_already_liked_items=True)
                            user_recs[uid] = [(item_ids[i], float(score)) for i, score in recs]
                        except Exception:
                            user_recs[uid] = []
                    print(f"Trained ALS f={f}, reg={reg}, it={it}")
                    return user_recs
                except Exception as e:
                    print("ALS train failed for", f, reg, it, "->", e)
                    continue
    return {}

# ---------------------------
# Rank-normalize + hybrid combining (switching for cold users)
# ---------------------------
def normalize_map_rank(score_map):
    normalized = {}
    for uid, lst in score_map.items():
        if not lst:
            normalized[uid] = []
            continue
        lst_sorted = sorted(lst, key=lambda x:-x[1])
        n = len(lst_sorted)
        normalized[uid] = [(lst_sorted[i][0], float((n - i) / n)) for i in range(n)]
    return normalized

def combine_hybrid_rank(content_map, behavior_map, alpha=ALPHA, beta=BETA, top_k=TOP_K, switching_threshold=5):
    c_rank = normalize_map_rank(content_map)
    b_rank = normalize_map_rank(behavior_map)
    all_users = set(c_rank.keys()) | set(b_rank.keys())
    hybrid = {}
    for uid in all_users:
        c_list = {jid:s for jid,s in c_rank.get(uid,[])}
        b_list = {jid:s for jid,s in b_rank.get(uid,[])}
        if not b_list or len(b_list) < switching_threshold:
            # fallback to content-based for cold users or missing behavior
            combined = [(jid, s) for jid,s in c_list.items()]
        else:
            jobs = set(c_list.keys()) | set(b_list.keys())
            combined = [(jid, alpha * c_list.get(jid,0) + beta * b_list.get(jid,0)) for jid in jobs]
        combined.sort(key=lambda x:-x[1])
        hybrid[uid] = combined[:top_k]
    return hybrid

# ---------------------------
# Persist & cache
# ---------------------------
def persist_mysql(hybrid_map):
    rows = [(uid,jid,score,'hybrid') for uid,lst in hybrid_map.items() for jid,score in lst]
    if not rows:
        return
    with engine.begin() as conn:
        conn.exec_driver_sql("""
            INSERT INTO recommendation_job(student_id,job_id,score,model_type,updated_at)
            VALUES (%s,%s,%s,%s,NOW())
            ON DUPLICATE KEY UPDATE score=VALUES(score), updated_at=NOW()
        """, rows)

def cache_redis(hybrid_map, ttl=7200):
    pipe = r.pipeline()
    for uid,lst in hybrid_map.items():
        key = f"reco:student:{uid}"
        pipe.delete(key)
        if lst:
            zmapping = {str(jid):score for jid,score in lst}
            pipe.zadd(key, zmapping)
            pipe.expire(key, ttl)
    pipe.execute()

def publish_kafka(hybrid_map):
    for uid,lst in hybrid_map.items():
        payload = {"student_id": uid, "jobs": [{"job_id":jid,"score":score} for jid,score in lst]}
        try:
            producer.send(KAFKA_TOPIC, payload)
        except Exception as e:
            print(f"Error sending Kafka message for student {uid}: {e}")
    try:
        producer.flush()
    except Exception as e:
        print("Error flushing Kafka producer:", e)
    print(f"Published {len(hybrid_map)} students to Kafka topic {KAFKA_TOPIC}")


# ---------------------------
# Evaluation (add HitRate & F1)
# ---------------------------
def train_test_split_time_based(interactions, test_ratio=TIME_SPLIT_RATIO):
    """
    Time-based split per user:
    For each student, sort interactions by time and keep the last ceil(len*test_ratio) as test.
    Ensures no NaN indices and at least one train sample if possible.
    """
    if interactions.empty:
        return pd.DataFrame(columns=interactions.columns), pd.DataFrame(columns=interactions.columns)

    # reset index để tránh KeyError loc
    interactions = interactions.sort_values(["student_id", "created_at"]).reset_index(drop=True)
    train_list, test_list = [], []

    for uid, g in interactions.groupby("student_id"):
        n = len(g)
        if n == 1:
            # chỉ 1 interaction → train thôi
            train_list.append(g)
            continue

        n_test = max(1, int(np.ceil(n * test_ratio)))
        test_part = g.tail(n_test)
        train_part = g.iloc[:n - n_test]

        # nếu train rỗng, move 1 từ test về train
        if train_part.empty and n > 1:
            train_part = test_part.head(1)
            test_part = test_part.tail(n_test - 1)

        train_list.append(train_part)
        test_list.append(test_part)

    train_inter = pd.concat(train_list).reset_index(drop=True) if train_list else pd.DataFrame(columns=interactions.columns)
    test_inter = pd.concat(test_list).reset_index(drop=True) if test_list else pd.DataFrame(columns=interactions.columns)

    return train_inter, test_inter

def get_actual_jobs(test_interactions):
    return test_interactions.groupby('student_id')['job_id'].apply(list).to_dict()

def precision_at_k(recommended, actual, k=TOP_K):
    if not actual: return None
    recommended_ids = [jid for jid,_ in recommended[:k]]
    hits = sum([1 for jid in actual if jid in recommended_ids])
    return hits / k

def recall_at_k(recommended, actual, k=TOP_K):
    if not actual: return None
    recommended_ids = [jid for jid,_ in recommended[:k]]
    hits = sum([1 for jid in actual if jid in recommended_ids])
    return hits / len(actual)

def ndcg_at_k(recommended, actual, k=TOP_K):
    if not actual: return None
    recommended_ids = [jid for jid,_ in recommended[:k]]
    dcg = sum(1.0 / np.log2(i+2) for i,jid in enumerate(recommended_ids) if jid in actual)
    idcg = sum(1.0 / np.log2(i+2) for i in range(min(len(actual), k)))
    return dcg / idcg if idcg>0 else 0.0

def map_at_k(recommended, actual, k=TOP_K):
    if not actual: return None
    recommended_ids = [jid for jid,_ in recommended[:k]]
    score = 0.0
    hits = 0
    for i,jid in enumerate(recommended_ids):
        if jid in actual:
            hits += 1
            score += hits / (i+1)
    return score / min(len(actual), k)

def hitrate_at_k(recommended, actual, k=TOP_K):
    if not actual: return None
    recommended_ids = [jid for jid,_ in recommended[:k]]
    return 1.0 if any(j in actual for j in recommended_ids) else 0.0

def f1_at_k(recommended, actual, k=TOP_K):
    if not actual: return None
    p = precision_at_k(recommended, actual, k)
    r = recall_at_k(recommended, actual, k)
    if p + r == 0:
        return 0.0
    return 2 * p * r / (p + r)

def evaluate_hybrid(hybrid_map, test_interactions, k=TOP_K):
    actual_map = get_actual_jobs(test_interactions)
    precisions, recalls, ndcgs, maps, hits, f1s = [], [], [], [], [], []
    for student_id, recommended in hybrid_map.items():
        actual = actual_map.get(student_id, [])
        if not actual:
            continue
        p = precision_at_k(recommended, actual, k)
        r = recall_at_k(recommended, actual, k)
        n = ndcg_at_k(recommended, actual, k)
        m = map_at_k(recommended, actual, k)
        h = hitrate_at_k(recommended, actual, k)
        f = f1_at_k(recommended, actual, k)
        if p is not None:
            precisions.append(p); recalls.append(r); ndcgs.append(n); maps.append(m); hits.append(h); f1s.append(f)
    avg_p = float(np.mean(precisions)) if precisions else 0.0
    avg_r = float(np.mean(recalls)) if recalls else 0.0
    avg_n = float(np.mean(ndcgs)) if ndcgs else 0.0
    avg_m = float(np.mean(maps)) if maps else 0.0
    avg_h = float(np.mean(hits)) if hits else 0.0
    avg_f = float(np.mean(f1s)) if f1s else 0.0
    print(f"Evaluation @ {k}: Precision={avg_p:.4f}, Recall={avg_r:.4f}, NDCG={avg_n:.4f}, MAP={avg_m:.4f}, HR={avg_h:.4f}, F1={avg_f:.4f}")
    return avg_p, avg_r, avg_n, avg_m, avg_h, avg_f

# ---------------------------
# Debug info
# ---------------------------
def debug_data(jobs, students, interactions):
    print("Unique jobs:", jobs['id'].nunique())
    print("Unique students:", students['student_id'].nunique())
    print("Interactions total:", len(interactions))
    if not interactions.empty:
        print("Interactions per student stats:", interactions.groupby('student_id').size().describe())
    print("Behavior type counts:", interactions['behavior_type'].value_counts().to_dict())

# ---------------------------
# Main train 
# ---------------------------
def train():
    start = time.time()
    print("Loading data...")
    jobs, students, interactions = load_data()
    debug_data(jobs, students, interactions)
    print(f"jobs={len(jobs)}, students={len(students)}, interactions={len(interactions)}")

    # ---------------- Time-based split ----------------
    train_inter, test_inter = train_test_split_time_based(interactions, test_ratio=TIME_SPLIT_RATIO)
    print(f"train_inter={len(train_inter)}, test_inter={len(test_inter)}")

    # ---------------- Build models ----------------
    pop_map, pop_by_field_map = compute_popularity(train_inter, jobs)
    user_idx, item_idx, mat, user_ids, item_ids = build_interaction_matrix(train_inter, students, jobs)
    behavior_map = train_als(user_idx, item_idx, mat, user_ids, item_ids, TOP_K)

    job_vec, student_vec, vec = build_tfidf(jobs, students)
    content_map = compute_content_topk(jobs, students, job_vec, student_vec,
                                       TOP_K, pop_map, pop_by_field_map, vec)

    hybrid_map = combine_hybrid_rank(content_map, behavior_map, ALPHA, BETA, TOP_K)
    persist_mysql(hybrid_map)
    cache_redis(hybrid_map)
    publish_kafka(hybrid_map)

    evaluate_hybrid(hybrid_map, test_inter, TOP_K)

    print(f"Total training time: {time.time()-start:.2f}s")

if __name__ == "__main__":
    import time

    INTERVAL_HOURS = 3  # chạy mỗi 3 tiếng
    INTERVAL_SECONDS = INTERVAL_HOURS * 3600

    while True:
        start_time = time.time()
        try:
            print("=== START TRAINING ===", time.strftime("%Y-%m-%d %H:%M:%S"))
            train()
            print("=== TRAINING DONE ===", time.strftime("%Y-%m-%d %H:%M:%S"))
        except Exception as e:
            print("Error during training:", e)
        
        elapsed = time.time() - start_time
        sleep_time = max(0, INTERVAL_SECONDS - elapsed)
        print(f"Sleeping for {sleep_time/60:.1f} minutes before next run...")
        time.sleep(sleep_time)

