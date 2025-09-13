package com.demo.autocareer.controller.student;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.autocareer.dto.RecommendationDTOResponse;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.BasePageRequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.ResponseData;
import com.demo.autocareer.service.RecommendationService;

@RestController
@RequestMapping("/api/student")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/recommendation")
    public ResponseEntity<ResponseData<BasePageResponse<RecommendationDTOResponse>>> getRecommendation(
            @RequestParam(defaultValue = "20") int topK,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        BasePageResponse<RecommendationDTOResponse> recommendations =
                recommendationService.getRecommendation(topK, pageable);

        ResponseData<BasePageResponse<RecommendationDTOResponse>> response =
                new ResponseData<>(HttpStatus.OK.value(), "OK", recommendations);

        return ResponseEntity.ok(response);
    }
}

