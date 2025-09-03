package com.demo.autocareer.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.demo.autocareer.dto.request.ApplyDTORequest;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.InternshipApprovedDTORequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.ApplyRequestDTOReponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.CompanyStaticDTOResponse;
import com.demo.autocareer.dto.response.InternshipApprovedDTOResponse;
import com.demo.autocareer.dto.response.InternshipRequestDTOResponse;
import com.demo.autocareer.dto.response.JobDTOResponse;
import com.demo.autocareer.dto.response.StudentDTOResponse;
import com.demo.autocareer.exception.ErrorCode;
import com.demo.autocareer.filter.InternshipRequestFilter;
import com.demo.autocareer.mapper.ApplyJobMapper;
import com.demo.autocareer.mapper.ApplyJobRequestMapper;
import com.demo.autocareer.mapper.InternshipApprovedMapper;
import com.demo.autocareer.mapper.InternshipMapper;
import com.demo.autocareer.mapper.JobMapper;
import com.demo.autocareer.mapper.OrganizationMapper;
import com.demo.autocareer.model.ApplyJob;
import com.demo.autocareer.model.InternshipRequest;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.JobProvince;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.enums.ApplyJobStatus;
import com.demo.autocareer.model.enums.JobStatus;
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
    @Autowired
    private JobDetailRepository jobRepository;
    @Autowired
    private ApplyJobRequestMapper applyJobRequestMapper;

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
    public OrganizationDTO getProfileCompany(){
        Organization company = getCompanyFromToken();
        OrganizationDTO dto = organizationMapper.mapEntityToResponse(company);
        return dto;
    }
    
    @Override
    public BasePageResponse<JobDTOResponse> getAllJobs(BaseFilterRequest request,Pageable pageable){
        Organization company = getCompanyFromToken();
        if (request.getFilters() != null) {
            request.setFilters(new HashMap<>(request.getFilters())); // clone
            request.getFilters().remove("jobStatus"); // enumField đang dùng
        }
        Specification<Job> spec = baseSpecification
                .build(request, "title", "jobStatus", "createdAt", null, null, null)
                .and((root, query, cb) -> cb.equal(root.get("organization"), company));
        Sort sort = baseSpecification.buildSort(request, "title");
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

            if (request.getEnumValue() != null && !request.getEnumValue().isBlank()) {
                predicates.add(cb.equal(root.get("applyJobStatus"), ApplyJobStatus.valueOf(request.getEnumValue())));
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
                    .build(request, "organizationName", null, null, null, null, null)
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
        
        StatusRequest currentStatus = internRequest.getStatusRequest();
        StatusRequest newStatus = request.getStatusRequest();

        switch (currentStatus) {
            case PENDING:
                if (newStatus != StatusRequest.APPROVED && newStatus != StatusRequest.REJECTED) {
                    throw ExceptionUtil.fromErrorCode(ErrorCode.INVALID_STATUS_TRANSITION);
                }
                break;

            case APPROVED:
                if (newStatus != StatusRequest.COMPLETED) {
                    throw ExceptionUtil.fromErrorCode(ErrorCode.INVALID_STATUS_TRANSITION);
                }
                break;

            case REJECTED:
            case COMPLETED:
                throw ExceptionUtil.fromErrorCode(ErrorCode.INVALID_STATUS_TRANSITION);

            default:
                throw ExceptionUtil.fromErrorCode(ErrorCode.INVALID_STATUS_TRANSITION);
        }

        String approvedBy = SecurityContextHolder.getContext().getAuthentication().getName();
        internRequest.setStatusRequest(newStatus);
        internRequest.setNote(request.getNote());
        internRequest.setApprovedBy(approvedBy);
        internshipRequestRepository.save(internRequest);
        return internshipApprovedMapper.mapEntityToResponse(internRequest);
    }

    @Override
    public BasePageResponse<InternshipRequestDTOResponse> getInternshipRequest(InternshipRequestFilter request, Pageable pageable){
        Organization company = getCompanyFromToken();
        Specification<InternshipRequest> spec = baseSpecificationInternship
            .build(request, "title", "statusRequest", null, null, null, null)
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

    @Override
    public CompanyStaticDTOResponse getStaticCompany(){
        Organization company = getCompanyFromToken();
        Long companyId = company.getId();
        long totalJobs = jobRepository.countAllByOrganization_Id(companyId);
        long activeJobs = jobRepository.countByOrganization_IdAndJobStatus(companyId, JobStatus.OPEN);
        long expiredJobs = jobRepository.countByOrganization_IdAndJobStatus(companyId, JobStatus.CLOSING);
        long pendingJobs = jobRepository.countByOrganization_IdAndJobStatus(companyId, JobStatus.PENDING);

        long totalApplicants = applyJobRepository.countByJob_Organization_Id(companyId);
        long hiredApplicants = applyJobRepository.countByJob_Organization_IdAndApplyJobStatus(companyId, ApplyJobStatus.ACCEPTED);

        Map<Integer, Long> monthlyStats = new HashMap<>();
        for (Object[] row : jobRepository.countJobsByMonth(companyId)) {
            monthlyStats.put((Integer) row[0], (Long) row[1]);
        }

        return CompanyStaticDTOResponse.builder()
                .totalJobs(totalJobs)
                .activeJobs(activeJobs)
                .expiredJobs(expiredJobs)
                .pendingJobs(pendingJobs)
                .totalApplicants(totalApplicants)
                .hiredApplicants(hiredApplicants)
                .monthlyJobStats(monthlyStats)
                .build();
    }

    @Override
    public ApplyJobDTOResponse getDetail(Long id){
        Organization company = getCompanyFromToken();
        ApplyJob applyJob = applyJobRepository.findById(id)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.APPLY_JOB_NOT_FOUND));
        return applyJobMapper.mapEntityToResponse(applyJob);
    }

    @Override
    public ApplyRequestDTOReponse handelApplyJob(Long id, ApplyDTORequest request){
        Organization company = getCompanyFromToken();
        ApplyJob applyJob = applyJobRepository.findById(id)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.APPLY_JOB_NOT_FOUND));
        if (request.getApplyJobStatus() == null) {
            throw ExceptionUtil.fromErrorCode(ErrorCode.STATUS_REQUIRED);
        }
        String approvedBy = SecurityContextHolder.getContext().getAuthentication().getName();
        applyJob.setJobStatus(request.getApplyJobStatus());
        applyJob.setApproved(approvedBy);
        applyJobRepository.save(applyJob);
        return applyJobRequestMapper.mapEntityToResponse(applyJob);
    }

    @Override
    public void deleteApplyJob(Long id){
        ApplyJob applyJob = applyJobRepository.findById(id)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.APPLY_JOB_NOT_FOUND));
        applyJobRepository.delete(applyJob);
    }
}
