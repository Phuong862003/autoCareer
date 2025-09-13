package com.demo.autocareer.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import com.demo.autocareer.dto.RecommendationDTOResponse;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.exception.ErrorCode;
import com.demo.autocareer.mapper.RecommendationMapper;
import com.demo.autocareer.mapper.JobMapper;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.RecommendationJob;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.repository.JobDetailRepository;
import com.demo.autocareer.repository.RecommendationRepository;
import com.demo.autocareer.repository.StudentRepository;
import com.demo.autocareer.service.RecommendationService;
import com.demo.autocareer.specification.BaseSpecification;
import com.demo.autocareer.utils.ExceptionUtil;
import com.demo.autocareer.utils.JwtUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.demo.autocareer.utils.PageUtils;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    private static final Logger log = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private static final int REDIS_TOP_K = 20; // top K jobs

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private JobDetailRepository jobRepository;

    @Autowired
    private RecommendationMapper recommendationMapper;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final BaseSpecification<RecommendationJob> baseSpecification = new BaseSpecification<>();

    @Override
    public Student getStudentFromToken() {
        String email = jwtUtil.getCurrentUserEmail();
        return studentRepository.findByUserEmail(email)
                .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.STUDENT_NOT_FOUND));
    }

    private Map<Long, Double> combineBatchAndRealtime(Long studentId, int topK) {
        String batchKey = "reco:student:" + studentId;
        String realtimeKey = "reco:student:realtime:" + studentId;

        Map<Long, Double> map = new HashMap<>();

        // Batch
        Set<ZSetOperations.TypedTuple<Object>> batchSet =
                redisTemplate.opsForZSet().reverseRangeWithScores(batchKey, 0, topK - 1);
        if (batchSet != null) {
            for (ZSetOperations.TypedTuple<Object> t : batchSet) {
                map.put(((Number) t.getValue()).longValue(), t.getScore());
            }
        }

        // Realtime
        Set<ZSetOperations.TypedTuple<Object>> realtimeSet =
                redisTemplate.opsForZSet().reverseRangeWithScores(realtimeKey, 0, -1);
        if (realtimeSet != null) {
            for (ZSetOperations.TypedTuple<Object> t : realtimeSet) {
                Long jobId = ((Number) t.getValue()).longValue();
                Double score = t.getScore();
                map.put(jobId, map.getOrDefault(jobId, 0.0) + score);
            }
        }

        // Sort giảm dần, giữ topK
        return map.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topK)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a,b) -> a,
                        LinkedHashMap::new
                ));
    }

    @Override
    public BasePageResponse<RecommendationDTOResponse> getRecommendation(int topK, Pageable pageable) {
        Student student = getStudentFromToken();
        Map<Long, Double> combinedScores = combineBatchAndRealtime(student.getId(), REDIS_TOP_K);

        // Nếu Redis trống → fallback DB
        if (combinedScores.isEmpty()) {
            log.warn("Redis empty for student {}, fallback DB", student.getId());

            Page<RecommendationJob> page = recommendationRepository.findByStudentId(student.getId(), pageable);
            if (page.isEmpty()) {
                return PageUtils.fromPage(Page.empty(pageable)); // chưa có gợi ý trong DB
            }

            List<RecommendationDTOResponse> dbDtos = page.getContent().stream()
                    .map(r -> {
                        Job job = r.getJob();
                        RecommendationDTOResponse dto = new RecommendationDTOResponse();
                        dto.setId(job.getId());
                        dto.setJob(jobMapper.mapEntityToResponse(job));
                        dto.setScore(r.getScore());
                        return dto;
                    })
                    .toList();

            // Ghi ngược lại Redis để lần sau nhanh hơn
            String batchKey = "reco:student:" + student.getId();
            for (RecommendationDTOResponse dto : dbDtos) {
                redisTemplate.opsForZSet().add(batchKey, dto.getId(), dto.getScore());
            }
            redisTemplate.expire(batchKey, java.time.Duration.ofHours(6));

            return PageUtils.fromPage(new PageImpl<>(dbDtos, pageable, page.getTotalElements()));
        }

        // Nếu Redis có dữ liệu → xử lý như cũ
        List<RecommendationDTOResponse> allDtos = combinedScores.entrySet().stream()
                .map(entry -> {
                    Job job = jobRepository.findById(entry.getKey()).orElse(null);
                    if (job == null) return null;

                    RecommendationDTOResponse dto = new RecommendationDTOResponse();
                    dto.setId(job.getId());
                    dto.setJob(jobMapper.mapEntityToResponse(job));
                    dto.setScore(entry.getValue());
                    return dto;
                })
                .filter(Objects::nonNull)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allDtos.size());
        List<RecommendationDTOResponse> pagedList = start < end ? allDtos.subList(start, end) : List.of();

        return PageUtils.fromPage(new PageImpl<>(pagedList, pageable, allDtos.size()));
    }


}
