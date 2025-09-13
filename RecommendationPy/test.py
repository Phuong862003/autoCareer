import itertools, time
import numpy as np
from RecommendationTrain import (
    load_data, debug_data, train_test_split_leave_one_out,
    compute_popularity, build_interaction_matrix, train_als,
    build_tfidf, compute_content_topk, combine_hybrid, evaluate_hybrid
)

import time
import itertools
import numpy as np
from implicit.als import AlternatingLeastSquares

# giả sử bạn đã có sẵn các biến:
# train_matrix (CSR user-item train), test_interactions (dict),
# evaluate_model(model, train_matrix, test_interactions) → trả về precision, recall, ndcg, map


def grid_search_als(train_matrix, test_interactions):
    param_grid = {
        "factors": [64, 128],
        "regularization": [0.01, 0.05, 0.1],
        "iterations": [30, 50],
    }

    best_config = None
    best_score = -1

    for factors, reg, iters in itertools.product(
        param_grid["factors"],
        param_grid["regularization"],
        param_grid["iterations"],
    ):
        print(f"\n>>> Testing ALS with factors={factors}, reg={reg}, iters={iters}")
        start = time.time()

        model = AlternatingLeastSquares(
            factors=factors,
            regularization=reg,
            iterations=iters,
            use_gpu=False,
        )
        model.fit(train_matrix)

        precision, recall, ndcg, map_score = evaluate_model(
            model, train_matrix, test_interactions
        )

        print(
            f"Precision={precision:.4f}, Recall={recall:.4f}, NDCG={ndcg:.4f}, MAP={map_score:.4f}, Time={time.time()-start:.2f}s"
        )

        # chọn theo Recall hoặc Precision tùy bạn
        if recall > best_score:
            best_score = recall
            best_config = {
                "factors": factors,
                "regularization": reg,
                "iterations": iters,
                "Precision": precision,
                "Recall": recall,
                "NDCG": ndcg,
                "MAP": map_score,
            }

    print("\n=== BEST CONFIG ===")
    for k, v in best_config.items():
        print(f"{k}: {v}")


if __name__ == "__main__":
    # TODO: import hàm load dữ liệu từ RecommendationTrain.py
    # train_matrix, test_interactions = load_data()
    grid_search_als(train_matrix, test_interactions)
