package com.demo.autocareer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.JobDTORequest;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.JobDTOResponse;
import com.demo.autocareer.exception.ErrorCode;
import com.demo.autocareer.mapper.JobMapper;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.repository.JobDetailRepository;
import com.demo.autocareer.repository.OrganizationRepository;
import com.demo.autocareer.service.JobDetailService;
import com.demo.autocareer.specification.BaseSpecification;
import com.demo.autocareer.utils.ExceptionUtil;
import com.demo.autocareer.utils.JwtUtil;
import com.demo.autocareer.utils.PageUtils;

@Service
public class JobDetailServiceImpl implements JobDetailService{
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private JobDetailRepository jobDetailRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JobMapper jobMapper;

    private final BaseSpecification<Job> baseSpecification = new BaseSpecification<>();

    @Override
    public Organization getCompanyFromToken() {
        String email = jwtUtil.getCurrentUserEmail(); 
        return organizationRepository.findByUserEmail(email)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.COMPANY_NOT_FOUND));
    }

    @Override
    public JobDTOResponse createJob(JobDTORequest request){
        Organization company = getCompanyFromToken();

        Job job = jobMapper.mapRequestToEntity(request);
        job.setOrganization(company);

        jobDetailRepository.save(job);
        return jobMapper.mapEntityToResponse(job);
    }

    @Override
    public JobDTOResponse updateJob(Long id, JobDTORequest request){
        Organization company = getCompanyFromToken();
        Job job = jobDetailRepository.findById(id)
                .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.JOB_NOT_FOUND));
        
        jobMapper.partialUpdate(job, request);
        job.setOrganization(company);
        jobDetailRepository.save(job);
        return jobMapper.mapEntityToResponse(job);
    }

    @Override
    public void deleteJob(Long id){
        Job job = jobDetailRepository.findById(id)
                .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.JOB_NOT_FOUND));
        
        jobDetailRepository.delete(job);
    }

    @Override
    public BasePageResponse<JobDTOResponse> getAllJobs(BaseFilterRequest request, Pageable pageable){
        Specification<Job> spec = baseSpecification
                .build(request, "title", "jobStatus", "createdAt", null, "jobProvinces.province");
        Sort sort = baseSpecification.buildSort(request, "title");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Job> jobs = jobDetailRepository.findAll(spec, sortPageable);

        Page<JobDTOResponse> jobDTOPage = jobs.map(jobMapper::mapEntityToResponse);

        return PageUtils.fromPage(jobDTOPage);
    }

}
