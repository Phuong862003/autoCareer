package com.demo.autocareer.service;

import java.util.Map;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@EnableKafka
public class UserBehaviorConsumer {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Map<String, Double> EVENT_SCORE = Map.of(
        "VIEW", 1.0,
        "CLICK", 2.0,
        "SAVE", 3.0,
        "APPLY", 5.0
    );

    @KafkaListener(topics = "user-behavior-topic", groupId = "reco-group")
    public void consume(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(message);
            Long studentId = node.get("studentId").asLong();
            Long jobId = node.get("jobId").asLong();
            String event = node.get("event").asText();

            double score = EVENT_SCORE.getOrDefault(event, 0.0);

            // Key cho realtime Redis
            String key = "reco:student:realtime:" + studentId;
            redisTemplate.opsForZSet().incrementScore(key, jobId, score);

            // Optional: set TTL để tránh Redis tăng vô hạn
            redisTemplate.expire(key, Duration.ofDays(7));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
