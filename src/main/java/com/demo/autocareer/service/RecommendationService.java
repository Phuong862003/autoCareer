package com.demo.autocareer.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.RecommendationDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.model.Student;

public interface RecommendationService {
    Student getStudentFromToken();
    BasePageResponse<RecommendationDTOResponse> getRecommendation(int topK, Pageable pageable);
}
