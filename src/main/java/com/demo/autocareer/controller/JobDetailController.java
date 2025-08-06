package com.demo.autocareer.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.autocareer.service.JobDetailService;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.BasePageRequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.JobDTOResponse;
import com.demo.autocareer.dto.response.ResponseData;


@RestController
@RequestMapping("/api/jobs")
public class JobDetailController {
    private final JobDetailService jobDetailService;
    public JobDetailController(JobDetailService jobDetailService) {
        this.jobDetailService = jobDetailService;
    }

    @GetMapping("/list")
    public ResponseData<BasePageResponse<JobDTOResponse>> getApplyJob(
                                    @ModelAttribute BaseFilterRequest baseFilterRequest,
                                    @ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse<JobDTOResponse> result = jobDetailService.getAllJobs(baseFilterRequest, pageable);
        return ResponseData.<BasePageResponse<JobDTOResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("GET APPLY JOB SUCCESS")
                .data(result)
                .build();
    }
}
