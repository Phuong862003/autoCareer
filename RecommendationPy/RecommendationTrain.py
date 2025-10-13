# train_hybrid_kafka_optimized_v2.py
# Improved & patched version of your hybrid training pipeline.
# Changes: use FACULTY_TO_FIELD_MAP to map faculty -> industry groups automatically
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
except Exception:
    IMPLICIT = False

from kafka import KafkaProducer

# ---------------------------
# Config (tune these)
# ---------------------------
REDIS_PORT = int(os.getenv("REDIS_PORT", 6378))
MYSQL_URI = os.getenv("MYSQL_URI", "mysql+pymysql://root:715826@localhost:3306/internlink")
REDIS_HOST = os.getenv("REDIS_HOST", "localhost")
KAFKA_BOOTSTRAP = os.getenv("KAFKA_BOOTSTRAP", "localhost:29092")
KAFKA_TOPIC = os.getenv("KAFKA_TOPIC", "student_recommendation")

TOP_K = int(os.getenv("TOP_K", 20))
ALPHA = float(os.getenv("ALPHA", 0.4))  # content weight
BETA = float(os.getenv("BETA", 0.6))    # behavior weight
TFIDF_MODEL_PATH = os.getenv("TFIDF_MODEL_PATH", "models/tfidf_vec.joblib")

# Adjusted event weights (SEARCH/CLICK up a bit, SAVE/APPLY important)
EVENT_WEIGHTS = {'SEARCH': 0.5, 'CLICK': 1.0, 'SAVE': 3.0, 'APPLY': 8.0}

# ==========================================
# Mapping giữa faculty_name và field_name
# ==========================================
FACULTY_TO_FIELD_MAP = {
    # --- CNTT & Kỹ thuật ---
    "Khoa Công nghệ thông tin": ["IT - Phần mềm", "IT - Phần cứng", "Viễn thông"],
    "Khoa Kỹ thuật phần mềm": ["IT - Phần mềm"],
    "Khoa Hệ thống thông tin": ["IT - Phần mềm"],
    "Khoa Mạng máy tính và Truyền thông": ["IT - Phần mềm", "IT - Phần cứng", "Viễn thông"],
    "Khoa Khoa học máy tính": ["IT - Phần mềm", "IT - Phần cứng"],
    "Khoa Công nghệ thông tin truyền thông": ["IT - Phần mềm", "Viễn thông"],
    "Khoa Điện - Điện tử": ["Điện tử/Điện lạnh", "IT - Phần cứng"],
    "Khoa Cơ điện tử": ["Sản xuất", "Điện tử/Điện lạnh", "IT - Phần cứng"],
    "Khoa Cơ khí": ["Sản xuất"],
    "Khoa Tự động hóa": ["Sản xuất", "IT - Phần cứng"],
    "Khoa Xây dựng": ["Sản xuất"],
    "Khoa Kiến trúc": ["Thiết kế", "Xây dựng"],
    "Khoa Kỹ thuật công trình": ["Xây dựng", "Sản xuất"],
    "Khoa Môi trường": ["Môi trường"],
    "Khoa Dầu khí": ["Sản xuất"],
    "Khoa Hóa học": ["Sản xuất"],
    "Khoa Vật liệu": ["Sản xuất"],
    "Khoa Sinh học": ["Dược phẩm/Y tế", "Môi trường"],
    "Khoa Kỹ thuật y sinh": ["Dược phẩm/Y tế"],
    "Khoa Kỹ thuật xét nghiệm y học": ["Dược phẩm/Y tế"],

    # --- Kinh tế & Quản trị ---
    "Khoa Kinh tế": ["Tài chính", "Chứng khoán", "Tư vấn"],
    "Khoa Quản trị kinh doanh": ["Tài chính", "Bán lẻ-Hàng tiêu dùng", "Marketing"],
    "Khoa Kế toán": ["Tài chính"],
    "Khoa Tài chính - Ngân hàng": ["Tài chính", "Chứng khoán", "Bảo hiểm"],
    "Khoa Marketing": ["Marketing", "Thương mại điện tử"],
    "Khoa Thương mại quốc tế": ["Xuất nhập khẩu", "Logistics-Vận tải"],
    "Khoa Kinh doanh quốc tế": ["Xuất nhập khẩu", "Logistics-Vận tải"],
    "Khoa Logistics và Chuỗi cung ứng": ["Logistics-Vận tải", "Xuất nhập khẩu"],
    "Khoa Quản lý công": ["Nhân sự/Hành chính"],
    "Khoa Luật": ["Luật"],
    "Khoa Luật Kinh tế": ["Luật"],
    "Khoa Quản lý giáo dục": ["Giáo dục và Đào tạo"],

    # --- Ngôn ngữ, Xã hội, Sư phạm ---
    "Khoa Ngôn ngữ Anh": ["Giáo dục và Đào tạo"],
    "Khoa Ngôn ngữ Pháp": ["Giáo dục và Đào tạo"],
    "Khoa Ngôn ngữ Nhật": ["Giáo dục và Đào tạo"],
    "Khoa Ngôn ngữ Trung": ["Giáo dục và Đào tạo"],
    "Khoa Ngôn ngữ Hàn Quốc": ["Giáo dục và Đào tạo"],
    "Khoa Việt Nam học": ["Giáo dục và Đào tạo"],
    "Khoa Đông phương học": ["Giáo dục và Đào tạo"],
    "Khoa Tây phương học": ["Giáo dục và Đào tạo"],
    "Khoa Giáo dục tiểu học": ["Giáo dục và Đào tạo"],
    "Khoa Giáo dục mầm non": ["Giáo dục và Đào tạo"],
    "Khoa Sư phạm Toán": ["Giáo dục và Đào tạo"],
    "Khoa Sư phạm Vật lý": ["Giáo dục và Đào tạo"],
    "Khoa Sư phạm Hóa học": ["Giáo dục và Đào tạo"],
    "Khoa Sư phạm Ngữ văn": ["Giáo dục và Đào tạo"],
    "Khoa Sư phạm Lịch sử": ["Giáo dục và Đào tạo"],
    "Khoa Sư phạm Địa lý": ["Giáo dục và Đào tạo"],

    # --- Nghệ thuật & Thiết kế ---
    "Khoa Mỹ thuật công nghiệp": ["Thiết kế"],
    "Khoa Thiết kế đồ họa": ["Thiết kế"],
    "Khoa Thiết kế thời trang": ["Thiết kế"],
    "Khoa Thiết kế nội thất": ["Thiết kế"],
    "Khoa Công nghệ in": ["Thiết kế"],
    "Khoa Công nghệ may": ["Thiết kế"],

    # --- Du lịch & Dịch vụ ---
    "Khoa Du lịch": ["Nhà hàng / Khách sạn"],
    "Khoa Quản trị khách sạn": ["Nhà hàng / Khách sạn"],
    "Khoa Quản trị nhà hàng": ["Nhà hàng / Khách sạn"],

    # --- Y Dược ---
    "Khoa Dược": ["Dược phẩm/Y tế"],
    "Khoa Y học": ["Dược phẩm/Y tế"],
    "Khoa Điều dưỡng": ["Dược phẩm/Y tế"],
    "Khoa Răng - Hàm - Mặt": ["Dược phẩm/Y tế"],

    # --- Báo chí, Truyền thông ---
    "Khoa Truyền thông và Báo chí": ["Marketing", "Thương mại điện tử"],
    "Khoa Quan hệ quốc tế": ["Tư vấn"],

    # --- Thể thao ---
    "Khoa Khoa học thể dục thể thao": ["Giáo dục và Đào tạo"],
}


# Normalize FACULTY_TO_FIELD_MAP keys/values with the same normalize_text used for text
def normalize_text(s):
    s = str(s).lower()
    # remove accents
    try:
        s = unicodedata.normalize('NFKD', s)
        s = ''.join([c for c in s if not unicodedata.combining(c)])
    except Exception:
        pass
    # keep alphanum and whitespace
    s = re.sub(r'[^0-9a-z\s\-\/]', ' ', s)
    s = re.sub(r'\s+', ' ', s).strip()
    # common normalizations
    s = s.replace('công nghệ thông tin', 'it')
    s = s.replace('cong nghe thong tin', 'it')
    s = s.replace('springboot', 'spring boot')
    return s

# Build a normalized faculty->fields map for quick lookup
FACULTY_TO_FIELD_MAP_NORM = {}
for k, vals in FACULTY_TO_FIELD_MAP.items():
    kn = normalize_text(k)
    FACULTY_TO_FIELD_MAP_NORM[kn] = [normalize_text(v) for v in vals]

# ALS tuning defaults (we will try reasonable configs)
ALS_FACTORS_TRY = [32, 64]
ALS_REG_TRY = [0.01, 0.08, 0.001]
ALS_ITERS_TRY = [20, 40]

# Candidate pool size from content before hybrid re-ranking
CONTENT_POOL_N = 200

# Faculty / skill boost multipliers (quick wins)
FACULTY_BOOST_SCALE = float(os.getenv("FACULTY_BOOST_SCALE", 0.20))
SKILL_BOOST_WEIGHT = float(os.getenv("SKILL_BOOST_WEIGHT", 0.45))

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
# Utilities (normalize_text already defined above)
# ---------------------------

# ---------------------------
# Load data
# ---------------------------
def load_data(min_interactions_per_student=3):
    with engine.connect() as conn:
        jobs = pd.read_sql("SELECT id,title,description,working_address,field_id FROM jobdetail", con=conn)

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

        interactions = pd.read_sql("SELECT student_id, job_id, behavior_type, COALESCE(`timestamp`, created_at) AS event_time FROM student_behavior", con=conn)

    jobs = jobs.fillna({'title':'','description':'','working_address':''})
    jobs['field_id'] = jobs['field_id'].astype('Int64')

    students['skills'] = students['skills'].fillna('')
    students['faculty_id'] = students['faculty_id'].astype('Int64')

    interactions['behavior_type'] = interactions['behavior_type'].astype(str).str.upper()
    interactions['weight'] = interactions['behavior_type'].map(EVENT_WEIGHTS).fillna(1.0)
    interactions = interactions.rename(columns={'event_time': 'created_at'})
    interactions['created_at'] = pd.to_datetime(interactions['created_at'], errors='coerce')

    # Filter students with at least N interactions to help ALS density
    interactions = interactions.groupby('student_id').filter(lambda x: len(x) >= min_interactions_per_student)

    return jobs, students, interactions

# ---------------------------
# TF-IDF content (improved)
# ---------------------------
def build_tfidf(jobs, students, interactions):
    # enrich jobs with field names and students with faculty names + recent job titles
    with engine.connect() as conn:
        try:
            field_df = pd.read_sql("SELECT id, field_name AS name FROM field", con=conn)
            field_map = dict(zip(
                field_df['id'].astype('Int64'),
                field_df['name'].astype(str).map(normalize_text)
            ))
        except Exception as e:
            print("Error loading field table:", e)
            field_map = {}

        try:
            faculty_df = pd.read_sql("SELECT id, faculty_name AS name FROM faculty", con=conn)
            faculty_map = dict(zip(
                faculty_df['id'].astype('Int64'),
                faculty_df['name'].astype(str).map(normalize_text)
            ))
        except Exception as e:
            print("Error loading faculty table:", e)
            faculty_map = {}


    # add field_name to jobs (normalized)
    jobs['field_name'] = jobs['field_id'].map(field_map).fillna('').astype(str)
    jobs['doc'] = (jobs['title'].fillna('') + ' ' + jobs['description'].fillna('') + ' ' + jobs['field_name'].fillna('')).map(normalize_text)

    # build recent job titles per student
    recent_titles = {}
    if not interactions.empty:
        job_titles = jobs.set_index('id')['title'].fillna('').to_dict()
        grouped = interactions.sort_values('created_at').groupby('student_id')['job_id'].apply(list)
        for sid, jlist in grouped.items():
            # take up to last 5
            last5 = jlist[-5:]
            titles = [normalize_text(job_titles.get(j, '')) for j in last5 if j in job_titles]
            recent_titles[int(sid)] = ' '.join(titles)

    def student_doc(row):
        parts = []
        skills = row.get('skills') or ''
        parts.append(skills)
        fid = row.get('faculty_id')
        if pd.notna(fid) and fid in faculty_map:
            parts.append(faculty_map[fid])
            # also add mapped industry groups (from FACULTY_TO_FIELD_MAP_NORM) into student's textual doc
            fac_norm = faculty_map[fid]
            mapped = FACULTY_TO_FIELD_MAP_NORM.get(fac_norm, [])
            if mapped:
                parts.extend(mapped)
        # add recent interacted job titles if any
        recent = recent_titles.get(int(row['student_id']), '')
        if recent:
            parts.append(recent)
        return normalize_text(' '.join(parts))

    students['doc'] = students.apply(student_doc, axis=1)

    corpus = pd.concat([jobs['doc'], students['doc']])

    vec = TfidfVectorizer(max_features=20000, ngram_range=(1,2), min_df=2, sublinear_tf=True, norm='l2')
    print("Fitting TF-IDF... corpus size:", len(corpus))
    vec.fit(corpus.values)
    job_vec = vec.transform(jobs['doc'].values)
    student_vec = vec.transform(students['doc'].values)
    job_vec = normalize(job_vec, axis=1, norm='l2', copy=False)
    student_vec = normalize(student_vec, axis=1, norm='l2', copy=False)
    os.makedirs('models', exist_ok=True)
    joblib.dump(vec, TFIDF_MODEL_PATH)

    # helpful maps for later
    job_field_map = jobs.set_index('id')['field_id'].to_dict()
    job_title_map = jobs.set_index('id')['title'].to_dict()

    # return faculty_map too so compute_content_topk can consult faculty name
    return job_vec, student_vec, vec, job_field_map, job_title_map, faculty_map

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


def compute_content_topk(jobs, students, job_vec, student_vec, top_k=TOP_K, pop_map=None, pop_by_field_map=None, vec=None, faculty_map=None):
    job_ids = jobs['id'].to_numpy()
    # job_field_ids = jobs['field_id'].fillna(-1).astype(int).to_numpy()
    job_field_names = jobs['field_name'].fillna('').to_numpy()
    results = {}

    job_term_sets = [set(row['doc'].split()) for _, row in jobs.iterrows()]
    student_term_sets = [set(row['doc'].split()) for _, row in students.iterrows()]

    with engine.connect() as conn:
        skill_set, skill_map = load_skill_list(conn)

    vocab = vec.vocabulary_
    idf = getattr(vec, "idf_", None)

    # Precompute job_field lookup dict
    job_field_lookup = dict(zip(jobs['id'].astype(int), jobs['field_id'].astype('Int64')))
    job_fieldname_lookup = dict(zip(jobs['id'].astype(int), jobs['field_name'].astype(str)))

    for i in tqdm(range(len(students))):
        student_id = int(students.iloc[i]['student_id'])
        fid = students.iloc[i]['faculty_id']
        faculty_id = int(fid) if pd.notna(fid) else -1

        # get normalized faculty name (if available)
        faculty_name_norm = None
        if faculty_map is not None and pd.notna(fid):
            faculty_name_norm = faculty_map.get(int(fid), None)

        # mapped industry groups from FACULTY_TO_FIELD_MAP_NORM
        mapped_fields = []
        if faculty_name_norm:
            mapped_fields = FACULTY_TO_FIELD_MAP_NORM.get(faculty_name_norm, [])

        s_vec = student_vec[i]
        sims = linear_kernel(s_vec, job_vec).ravel()

        # skill exact match boost (use normalized skill set)
        student_terms = student_term_sets[i]
        # only keep recognized skill tokens to avoid noise
        recognized_terms = {t for t in student_terms if t in skill_set}
        boost = np.zeros_like(sims, dtype=float)
        if recognized_terms:
            if idf is not None:
                denom = sum(idf[vocab[t]] for t in recognized_terms if t in vocab) + 1e-9
            else:
                denom = len(recognized_terms) + 1e-9

            for j in range(len(jobs)):
                inter = recognized_terms & job_term_sets[j]
                if inter:
                    if idf is not None:
                        boost_val = sum(idf[vocab[t]] for t in inter if t in vocab) / denom
                    else:
                        boost_val = len(inter) / denom
                    boost[j] = boost_val

        # combine base sims + skill boost
        scores = sims + (boost * SKILL_BOOST_WEIGHT)

        # same-field prioritization using mapped_fields (if available)
        same_fac_mask = np.zeros_like(sims, dtype=bool)
        if mapped_fields:
            # jobs have 'field_name' normalized; check membership
            jfield_arr = np.array(job_field_names, dtype=object)
            for mf in mapped_fields:
                same_fac_mask |= (jfield_arr == mf)
        else:
            # if no mapping available, fallback to faculty_id equality on numeric field_id (previous behavior)
            if faculty_id != -1:
                job_field_ids = jobs['field_id'].fillna(-1).astype(int).to_numpy()
                same_fac_mask = (job_field_ids == faculty_id)

        # boost scores for same-field matches slightly to prefer them
        # but still keep variety: multiply by (1 + FACULTY_BOOST_SCALE) for matches
        scores = np.where(same_fac_mask, scores * (1.0 + FACULTY_BOOST_SCALE), scores)

        # select candidates: prefer same-fac first (sorted by adjusted scores)
        pool_n = max(top_k, min(CONTENT_POOL_N, len(scores)))
        same_idxs = np.where(same_fac_mask)[0]
        other_idxs = np.where(~same_fac_mask)[0]

        same_sorted = same_idxs[np.argsort(-scores[same_idxs])] if same_idxs.size else np.array([], dtype=int)
        other_sorted = other_idxs[np.argsort(-scores[other_idxs])] if other_idxs.size else np.array([], dtype=int)

        selected = []
        # take top from same faculty first
        for idx in same_sorted[:pool_n]:
            if scores[idx] > 0:
                selected.append((int(job_ids[idx]), float(scores[idx])))

        # fill from others with slight penalty so same-fac remains preferred when similar
        if len(selected) < top_k:
            needed = top_k - len(selected)
            penalized_scores = scores[other_sorted] * 0.88
            other_order = other_sorted[np.argsort(-penalized_scores)]
            for idx in other_order[:max(needed, pool_n)]:
                if scores[idx] > 0:
                    selected.append((int(job_ids[idx]), float(scores[idx])))

        # fallback popularity if still not enough
        if len(selected) < top_k:
            seen = {jid for jid,_ in selected}
            needed = top_k - len(selected)
            added = []
            if faculty_id != -1 and pop_by_field_map and int(faculty_id) in pop_by_field_map:
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
            selected.extend(added[:needed])

        results[student_id] = selected[:top_k]

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

    # behavior weights (can tune)
    behavior_weights = {
        'SEARCH': 0.3,
        'CLICK': 1.0,
        'SAVE': 3.0,
        'APPLY': 5.0
    }
    inter['behavior_type'] = inter['behavior_type'].astype(str).str.upper()
    inter['weight'] = inter['behavior_type'].map(behavior_weights).fillna(1.0)

    now = inter['created_at'].max() if not inter['created_at'].isna().all() else pd.Timestamp.utcnow()

    def recency_factor(ts):
        days = (now - pd.to_datetime(ts)).days
        return 0.5 ** (days / recency_half_life_days)

    inter['recency_factor'] = inter['created_at'].apply(recency_factor)
    inter['w'] = inter['weight'] * (1.0 + inter['recency_factor'])

    agg = inter.groupby(['student_id','job_id'], as_index=False)['w'].sum().rename(columns={'w':'weight'})

    if len(agg) == 0:
        from scipy.sparse import csr_matrix
        return {}, {}, csr_matrix((0,0)), [], []

    # normalize weights to [0,1]
    agg['weight'] = (agg['weight'] - agg['weight'].min()) / (agg['weight'].max() - agg['weight'].min() + 1e-9)

    user_ids = sorted(students['student_id'].unique())
    item_ids = sorted(jobs['id'].unique())
    user_idx = {u:i for i,u in enumerate(user_ids)}
    item_idx = {j:i for i,j in enumerate(item_ids)}

    # keep only interactions where both user and item exist in our index
    orig_len = len(agg)
    agg = agg[agg['student_id'].isin(user_idx.keys()) & agg['job_id'].isin(item_idx.keys())]
    kept_len = len(agg)
    print(f"Interaction agg: original={orig_len}, kept_after_index_filter={kept_len}")

    from scipy.sparse import csr_matrix
    rows = agg['student_id'].map(user_idx).to_numpy()
    cols = agg['job_id'].map(item_idx).to_numpy()
    confidence = 1.0 + 40.0 * agg['weight'].to_numpy(dtype=float)

    mat = csr_matrix((confidence, (rows, cols)), shape=(len(user_ids), len(item_ids)))

    print(f"Built ALS matrix: shape={mat.shape}, nonzero={mat.nnz}, mean_conf={confidence.mean():.2f}")
    return user_idx, item_idx, mat, user_ids, item_ids


def train_als(user_idx, item_idx, mat, user_ids, item_ids, top_k=TOP_K,
              try_factors=ALS_FACTORS_TRY, try_reg=ALS_REG_TRY, try_iters=ALS_ITERS_TRY):
    if not IMPLICIT or mat.nnz==0:
        print("Skipping ALS — implicit not available or empty matrix")
        return {}
    print("Training ALS...")

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
                    model.fit((mat.T).tocsr().astype('double'))

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
    print("ALS training failed for all parameter sets")
    return {}

# ---------------------------
# Rank-normalize + hybrid combining
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
        # if behavior small or missing -> rely on content
        if not b_list or len(b_list) < switching_threshold:
            combined = [(jid, s) for jid,s in c_list.items()]
        else:
            jobs_set = set(c_list.keys()) | set(b_list.keys())
            combined = [(jid, alpha * c_list.get(jid,0) + beta * b_list.get(jid,0)) for jid in jobs_set]
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
# Evaluation (unchanged)
# ---------------------------
def train_test_split_time_based(interactions, test_ratio=TIME_SPLIT_RATIO):
    if interactions.empty:
        return pd.DataFrame(columns=interactions.columns), pd.DataFrame(columns=interactions.columns)

    interactions = interactions.sort_values(["student_id", "created_at"]).reset_index(drop=True)
    train_list, test_list = [], []

    for uid, g in interactions.groupby("student_id"):
        n = len(g)
        if n == 1:
            train_list.append(g)
            continue
        n_test = max(1, int(np.ceil(n * test_ratio)))
        test_part = g.tail(n_test)
        train_part = g.iloc[:n - n_test]
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

# metric functions unchanged
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
# Debug / inspection helpers
# ---------------------------
def inspect_user_recommendation(student_id, jobs, students, content_map, behavior_map):
    job_field_map = jobs.set_index('id')['field_id'].to_dict()
    job_title_map = jobs.set_index('id')['title'].to_dict()
    print(f"--- Inspect student {student_id} ---")
    rec = students[students['student_id']==student_id]
    if not rec.empty:
        print('Student record:', rec.to_dict('records'))
    print('\nTop content candidates:')
    for jid, score in content_map.get(student_id, [])[:10]:
        print(jid, job_field_map.get(jid), job_title_map.get(jid), score)
    print('\nTop behavior candidates:')
    for jid, score in behavior_map.get(student_id, [])[:10]:
        print(jid, job_field_map.get(jid), job_title_map.get(jid), score)

# ---------------------------
# Main train
# ---------------------------
def train():
    start = time.time()
    print("Loading data...")
    jobs, students, interactions = load_data()
    print(f"jobs={len(jobs)}, students={len(students)}, interactions={len(interactions)}")
    if interactions.empty:
        print("No interactions after filtering — aborting train")
        return

    train_inter, test_inter = train_test_split_time_based(interactions, test_ratio=TIME_SPLIT_RATIO)
    print(f"train_inter={len(train_inter)}, test_inter={len(test_inter)}")

    pop_map, pop_by_field_map = compute_popularity(train_inter, jobs)

    user_idx, item_idx, mat, user_ids, item_ids = build_interaction_matrix(train_inter, students, jobs)
    behavior_map = train_als(user_idx, item_idx, mat, user_ids, item_ids, TOP_K)

    job_vec, student_vec, vec, job_field_map, job_title_map, faculty_map = build_tfidf(jobs, students, train_inter)
    content_map = compute_content_topk(jobs, students, job_vec, student_vec,
                                       TOP_K, pop_map, pop_by_field_map, vec, faculty_map)

    hybrid_map = combine_hybrid_rank(content_map, behavior_map, ALPHA, BETA, TOP_K)

    # persist/cache/publish
    persist_mysql(hybrid_map)
    cache_redis(hybrid_map)
    publish_kafka(hybrid_map)

    evaluate_hybrid(hybrid_map, test_inter, TOP_K)

    # example inspection: show a few problematic users (optional)
    # find students with many content candidates but no matching field in top-K
    for sid in list(hybrid_map.keys())[:3]:
        inspect_user_recommendation(sid, jobs, students, content_map, behavior_map)

    print(f"Total training time: {time.time()-start:.2f}s")

if __name__ == "__main__":
    INTERVAL_HOURS = 3
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
