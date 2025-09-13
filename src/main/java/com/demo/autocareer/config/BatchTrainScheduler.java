package com.demo.autocareer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BatchTrainScheduler {
    private static final Logger log = LoggerFactory.getLogger(BatchTrainScheduler.class);
    @Scheduled(fixedRateString = "#{T(java.util.concurrent.TimeUnit).HOURS.toMillis(3)}")
    public void trainBatchModel() {
        log.info("🚀 Start batch training...");

        // 1️⃣ Lấy dữ liệu từ DB
        // 2️⃣ Train model (ALS / hybrid / any model)
        // 3️⃣ Update Redis batchKey
        // key = "reco:student:{studentId}"
        // opsForZSet().add(jobId, score)

        log.info("✅ Batch training finished");
    }
}
