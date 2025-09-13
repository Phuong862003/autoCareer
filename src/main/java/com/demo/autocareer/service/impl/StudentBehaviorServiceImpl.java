package com.demo.autocareer.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.demo.autocareer.dto.request.StudentBehaviorDTORequest;
import com.demo.autocareer.dto.response.StudentBehaviorDTOResponse;
import com.demo.autocareer.exception.ErrorCode;
import com.demo.autocareer.mapper.StudentBehaviorMapper;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.model.StudentBehavior;
import com.demo.autocareer.model.enums.BehaviorType;
import com.demo.autocareer.repository.JobDetailRepository;
import com.demo.autocareer.repository.StudentBehaviorRepository;
import com.demo.autocareer.repository.StudentRepository;
import com.demo.autocareer.service.StudentBehaviorService;
import com.demo.autocareer.utils.ExceptionUtil;
import com.demo.autocareer.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StudentBehaviorServiceImpl implements StudentBehaviorService {
    private static final Logger log = LoggerFactory.getLogger(StudentBehaviorServiceImpl.class);
    @Autowired private StudentBehaviorRepository behaviorRepository;
    @Autowired private JobDetailRepository jobRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired private RedisTemplate<String, Object> redisTemplate;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private StudentBehaviorMapper studentBehaviorMapper;

    private static final Map<BehaviorType, Double> EVENT_SCORE = Map.of(
        BehaviorType.VIEW, 1.0,
        BehaviorType.CLICK, 2.0,
        BehaviorType.SAVE, 3.0,
        BehaviorType.APPLY, 5.0
    );

    @Override
    public Student getStudentFromToken() {
        String email = jwtUtil.getCurrentUserEmail(); 
        return studentRepository.findByUserEmail(email)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.STUDENT_NOT_FOUND));
    }

    @Override
    public List<StudentBehaviorDTOResponse> recordBehavior(StudentBehaviorDTORequest request) {
        Student student = getStudentFromToken();

        List<StudentBehaviorDTOResponse> responses = new ArrayList<>();

        for (Long jobId : request.getJobId()) {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));

            double score = EVENT_SCORE.getOrDefault(request.getBehaviorType(), 0.0);

            // 1️⃣ Lưu DB
            StudentBehavior behavior = StudentBehavior.builder()
                    .student(student)
                    .job(job)
                    .behaviorType(request.getBehaviorType())
                    .timestamp(new Date())
                    .build();
            behaviorRepository.save(behavior);

            // 2️⃣ Kafka
            try {
                ObjectMapper mapper = new ObjectMapper();
                String message = mapper.writeValueAsString(Map.of(
                        "studentId", student.getId(),
                        "jobId", job.getId(),
                        "behaviorType", request.getBehaviorType().name(),
                        "score", score,
                        "timestamp", behavior.getTimestamp()
                ));
                kafkaTemplate.send("student-behavior-topic", message);
            } catch (Exception e) {
                log.error("Error sending Kafka message", e);
            }

            // 3️⃣ Redis
            String realtimeKey = "reco:student:realtime:" + student.getId();
            redisTemplate.opsForZSet().incrementScore(realtimeKey, job.getId(), score);
            redisTemplate.expire(realtimeKey, Duration.ofHours(6));

            responses.add(studentBehaviorMapper.toDTO(behavior));
        }

        return responses;
    }

}
