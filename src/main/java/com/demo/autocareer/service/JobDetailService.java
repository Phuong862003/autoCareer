package com.demo.autocareer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.JobDTORequest;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.JobDTOResponse;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.User;

public interface  JobDetailService {
    Organization getCompanyFromToken();
    JobDTOResponse createJob(JobDTORequest request);
    JobDTOResponse updateJob(Long id, JobDTORequest request);
    void deleteJob(Long id);
    BasePageResponse<JobDTOResponse> getAllJobs(BaseFilterRequest request, Pageable pageable);
}
