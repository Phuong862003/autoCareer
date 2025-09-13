package com.demo.autocareer.config;

import java.util.Map;
import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
// KafkaStudentRecommendationListener.java
@Component
@Slf4j
public class KafkaStudentRecommendationListener {
    private static final Logger log = LoggerFactory.getLogger(KafkaStudentRecommendationListener.class);


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @KafkaListener(
        topics = "student-behavior-topic",
        groupId = "user-behavior-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(Map<String, Object> message, Acknowledgment ack) {
        log.info("📩 Received message: {}", message);

        try {
            if (message.containsKey("jobs")) {
                // Batch recommendation (offline)
                Integer studentId = ((Number) message.get("studentId")).intValue();
                List<Map<String, Object>> jobs = (List<Map<String, Object>>) message.get("jobs");

                if (studentId == null || jobs == null) {
                    log.warn("Invalid batch message: {}", message);
                    ack.acknowledge();
                    return;
                }

                String realtimeKey = "reco:student:realtime:" + studentId;
                for (Map<String, Object> job : jobs) {
                    Long jobId = ((Number) job.get("job_id")).longValue();
                    Double score = ((Number) job.get("score")).doubleValue();
                    redisTemplate.opsForZSet().incrementScore(realtimeKey, jobId, score);
                }
                redisTemplate.expire(realtimeKey, Duration.ofHours(6));
                log.info("✅ Updated Realtime Redis cache (batch) for student {}", studentId);

            } else if (message.containsKey("jobId")) {
                // Realtime behavior
                Integer studentId = ((Number) message.get("studentId")).intValue();
                Long jobId = ((Number) message.get("jobId")).longValue();
                Double score = ((Number) message.get("score")).doubleValue();

                String realtimeKey = "reco:student:realtime:" + studentId;
                redisTemplate.opsForZSet().incrementScore(realtimeKey, jobId, score);
                redisTemplate.expire(realtimeKey, Duration.ofHours(6));

                log.info("✅ Updated Realtime Redis cache (behavior) for student {}", studentId);
            } else {
                log.warn("Unknown message format: {}", message);
            }
        } catch (Exception e) {
            log.error("❌ Failed to process message: {}", message, e);
        } finally {
            ack.acknowledge();
        }
    }
}
