package com.demo.autocareer.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.demo.autocareer.dto.OrganizationDTO;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.InternshipApprovedDTORequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.InternshipApprovedDTOResponse;
import com.demo.autocareer.dto.response.InternshipRequestDTOResponse;
import com.demo.autocareer.dto.response.JobDTOResponse;
import com.demo.autocareer.dto.response.StudentDTOResponse;
import com.demo.autocareer.exception.ErrorCode;
import com.demo.autocareer.filter.InternshipRequestFilter;
import com.demo.autocareer.mapper.ApplyJobMapper;
import com.demo.autocareer.mapper.InternshipApprovedMapper;
import com.demo.autocareer.mapper.InternshipMapper;
import com.demo.autocareer.mapper.JobMapper;
import com.demo.autocareer.mapper.OrganizationMapper;
import com.demo.autocareer.model.ApplyJob;
import com.demo.autocareer.model.InternshipRequest;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.JobProvince;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.enums.OrganizationType;
import com.demo.autocareer.model.enums.StatusRequest;
import com.demo.autocareer.repository.ApplyJobRepository;
import com.demo.autocareer.repository.InternshipRequestRepository;
import com.demo.autocareer.repository.JobDetailRepository;
import com.demo.autocareer.repository.OrganizationRepository;
import com.demo.autocareer.service.CompanyService;
import com.demo.autocareer.specification.BaseSpecification;
import com.demo.autocareer.utils.ExceptionUtil;
import com.demo.autocareer.utils.JwtUtil;
import com.demo.autocareer.utils.PageUtils;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

@Service
public class CompanyServiceImpl implements CompanyService{
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private JobDetailRepository jobDetailRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private ApplyJobRepository applyJobRepository;
    @Autowired
    private InternshipRequestRepository internshipRequestRepository;
    @Autowired
    private ApplyJobMapper applyJobMapper;
    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired
    private InternshipApprovedMapper internshipApprovedMapper;
    @Autowired
    private InternshipMapper internshipMapper;

    private final BaseSpecification<Job> baseSpecification = new BaseSpecification<>();
    private final BaseSpecification<Organization> baseSpecificationCompany = new BaseSpecification<>();
    private final BaseSpecification<InternshipRequest> baseSpecificationInternship = new BaseSpecification<>();

    @Override
    public Organization getCompanyFromToken() {
        String email = jwtUtil.getCurrentUserEmail(); 
        return organizationRepository.findByUserEmail(email)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.COMPANY_NOT_FOUND));
    }

    @Override
    public BasePageResponse<JobDTOResponse> getAllJobs(BaseFilterRequest request,Pageable pageable){
        Organization company = getCompanyFromToken();
        Specification<Job> spec = baseSpecification
                .build(request, "job.title", "jobStatus", "createdAt", null, null)
                .and((root, query, cb) -> cb.equal(root.get("organization"), company));
        Sort sort = baseSpecification.buildSort(request, "job.title");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Job> page = jobDetailRepository.findAll(spec, sortPageable);
        Page<JobDTOResponse> result = page.map(jobMapper::mapEntityToResponse);
        return PageUtils.fromPage(result);
    }

    @Override
    public BasePageResponse<ApplyJobDTOResponse> getApplications(BaseFilterRequest request, Pageable pageable){
        Organization company = getCompanyFromToken();
        Specification<ApplyJob> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Object, Object> jobJoin = root.join("job");

            predicates.add(cb.equal(jobJoin.get("organization"), company));

            if (request.getKeyword() != null) {
            predicates.add(cb.like(cb.lower(jobJoin.get("title")), "%" + request.getKeyword().toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = baseSpecification.buildSort(request, "job.title");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<ApplyJob> page = applyJobRepository.findAll(spec, sortPageable);
        Page<ApplyJobDTOResponse> result = page.map(applyJobMapper::mapEntityToResponse);
        return PageUtils.fromPage(result);
    }

    @Override
    public BasePageResponse<OrganizationDTO> getCompany(BaseFilterRequest request, Pageable pageable){
        Specification<Organization> spec = baseSpecificationCompany
                    .build(request, "organizationName", null, null, null, null)
                    .and((root, query, cb) -> cb.equal(root.get("organizationType"), OrganizationType.COMPANY));
        Sort sort = baseSpecificationCompany.buildSort(request, "organizationName");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Organization> page = organizationRepository.findAll(spec, sortPageable);
        Page<OrganizationDTO> result = page.map(organizationMapper::mapEntityToResponse);
        return PageUtils.fromPage(result);
    }

    @Override
    public BasePageResponse<JobDTOResponse> getJobCompany(Long id, BaseFilterRequest request, Pageable pageable){
        Organization company = organizationRepository.findById(id)
                .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.COMPANY_NOT_FOUND));
        Specification<Job> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(request.getProvinceId()!= null){
                Join<Job, JobProvince> jobProvince = root.join("jobProvinces", JoinType.LEFT);
                predicates.add(cb.equal(jobProvince.get("province").get("id"), request.getProvinceId()));
            }

            predicates.add(cb.equal(root.get("organization").get("id"), company.getId()));

            if (StringUtils.hasText(request.getKeyword())) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + request.getKeyword().toLowerCase() + "%"));
            }   
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = baseSpecification.buildSort(request, "title");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Job> page = jobDetailRepository.findAll(spec, sortPageable);
        Page<JobDTOResponse> result = page.map(jobMapper::mapEntityToResponse);
        return PageUtils.fromPage(result);
    }

    @Override
    public InternshipApprovedDTOResponse handelRequest(Long id, InternshipApprovedDTORequest request){
        Organization company = getCompanyFromToken();

        InternshipRequest internRequest = internshipRequestRepository.findById(id)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.COMPANY_NOT_FOUND));
        
        if (internRequest.getStatusRequest() != StatusRequest.PENDING){
            throw ExceptionUtil.fromErrorCode(ErrorCode.INTERN_REQUEST_PENDING);
        }

        String approvedBy = SecurityContextHolder.getContext().getAuthentication().getName();
        internRequest.setStatusRequest(request.getStatusRequest());
        internRequest.setNote(request.getNote());
        internRequest.setApprovedBy(approvedBy);
        internshipRequestRepository.save(internRequest);
        return internshipApprovedMapper.mapEntityToResponse(internRequest);
    }

    @Override
    public BasePageResponse<InternshipRequestDTOResponse> getInternshipRequest(InternshipRequestFilter request, Pageable pageable){
        Organization company = getCompanyFromToken();
        Specification<InternshipRequest> spec = baseSpecificationInternship
            .build(request, "title", "statusRequest", null, null, null)
            .and((root, query, cb) -> cb.equal(root.get("company"), company))
            .and((root, query, cb) -> {
                if (request.getUniversityId() != null) {
                    return cb.equal(root.get("university").get("id"), request.getUniversityId());
                }
                return cb.conjunction();
            });
        Sort sort = baseSpecification.buildSort(request, "title");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        
        Page<InternshipRequest> page = internshipRequestRepository.findAll(spec, sortPageable);
        Page<InternshipRequestDTOResponse> result = page.map(internshipMapper::mapEntityToResponse);
        return PageUtils.fromPage(result);
    }
}
