package com.demo.autocareer.service;

import org.springframework.data.domain.Pageable;

import com.demo.autocareer.dto.OrganizationDTO;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.InternshipApprovedDTORequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.CompanyStaticDTOResponse;
import com.demo.autocareer.dto.response.InternshipApprovedDTOResponse;
import com.demo.autocareer.dto.response.InternshipRequestDTOResponse;
import com.demo.autocareer.dto.response.JobDTOResponse;
import com.demo.autocareer.filter.InternshipRequestFilter;
import com.demo.autocareer.model.Organization;

public interface CompanyService {
    Organization getCompanyFromToken();
    OrganizationDTO getProfileCompany();
    BasePageResponse<JobDTOResponse> getAllJobs(BaseFilterRequest request,Pageable pageable);
    BasePageResponse<ApplyJobDTOResponse> getApplications(BaseFilterRequest request, Pageable pageable);
    BasePageResponse<OrganizationDTO> getCompany(BaseFilterRequest request, Pageable pageable);
    BasePageResponse<JobDTOResponse> getJobCompany(Long id, BaseFilterRequest request, Pageable pageable);
    InternshipApprovedDTOResponse handelRequest(Long id, InternshipApprovedDTORequest request);
    BasePageResponse<InternshipRequestDTOResponse> getInternshipRequest(InternshipRequestFilter request, Pageable pageable);
    CompanyStaticDTOResponse getStaticCompany();
}
