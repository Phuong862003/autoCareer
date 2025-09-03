package com.demo.autocareer.service.impl;

import java.util.List;

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
import com.demo.autocareer.model.Field;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.model.SubField;
import com.demo.autocareer.model.enums.JobStatus;
import com.demo.autocareer.repository.FieldRepository;
import com.demo.autocareer.repository.JobDetailRepository;
import com.demo.autocareer.repository.OrganizationRepository;
import com.demo.autocareer.repository.SubFieldRepository;
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
    @Autowired
    private SubFieldRepository subFieldRepository;
    @Autowired
    private FieldRepository fieldRepository;

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

        List<SubField> subFields = subFieldRepository.findAllById(request.getSubFieldIds());
        if (subFields.isEmpty()) {
            throw new IllegalArgumentException("Phải chọn ít nhất một chuyên ngành");
        }
        job.setSubFields(subFields);

        Field parentField = subFields.get(0).getField();
        job.setField(parentField);

        job.setJobStatus(JobStatus.OPEN);
        jobDetailRepository.save(job);
        return jobMapper.mapEntityToResponse(job);
    }

    @Override
    public JobDTOResponse updateJob(Long id, JobDTORequest request){
        Organization company = getCompanyFromToken();
        Job job = jobDetailRepository.findByIdAndOrganization(id, company)
                .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.JOB_NOT_FOUND));

        jobMapper.partialUpdate(job, request);

        // Update subfields chỉ khi request có subfieldIds
        if (request.getSubFieldIds() != null && !request.getSubFieldIds().isEmpty()) {
            List<SubField> subFields = subFieldRepository.findAllById(request.getSubFieldIds());
            job.setSubFields(subFields);
            Field parentField = subFields.get(0).getField();
            job.setField(parentField);
        }

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
    public BasePageResponse<JobDTOResponse> getAllJobs(BaseFilterRequest request, String salaryFilter, Pageable pageable) {
        Specification<Job> spec = baseSpecification
                .build(request, "title", "workingType", "createdAt", null, "jobProvinces.province", "field");

        if (salaryFilter != null && !salaryFilter.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                switch (salaryFilter) {
                    case "under10":
                        return cb.lessThan(root.get("salary_end"), 10);
                    case "10to20":
                        return cb.and(
                            cb.greaterThanOrEqualTo(root.get("salary_start"), 10),
                            cb.lessThanOrEqualTo(root.get("salary_end"), 20)
                        );
                    case "20to30":
                        return cb.and(
                            cb.greaterThanOrEqualTo(root.get("salary_start"), 20),
                            cb.lessThanOrEqualTo(root.get("salary_end"), 30)
                        );
                    case "over30":
                        return cb.greaterThan(root.get("salary_end"), 30);
                    default:
                        return null;
                }
            });
        }

        Sort sort = baseSpecification.buildSort(request, "createdAt");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Job> jobs = jobDetailRepository.findAll(spec, sortPageable);
        Page<JobDTOResponse> jobDTOPage = jobs.map(jobMapper::mapEntityToResponse);

        return PageUtils.fromPage(jobDTOPage);
    }


    @Override
    public JobDTOResponse getJobDetail(Long id){
        Organization company = getCompanyFromToken();
        Job job = jobDetailRepository.findByIdAndOrganization(id, company)
                .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.JOB_NOT_FOUND));
        if (!job.getOrganization().equals(company)) {
            throw ExceptionUtil.fromErrorCode(ErrorCode.ACCESS_DENIED);
        }

        List<SubField> jobSubfields = jobDetailRepository.findSubFieldsByJobId(id);

        Field field = job.getField();
        if (field != null) {
            // Chỉ set subfields nếu field tồn tại
            field.setSubFields(jobSubfields);
            job.setField(field);
        }
        return jobMapper.mapEntityToResponse(job);
    }

    @Override
    public JobDTOResponse getJobDetailPortal(Long id){
        Job job = jobDetailRepository.findById(id)
                .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.JOB_NOT_FOUND));
        List<SubField> jobSubfields = jobDetailRepository.findSubFieldsByJobId(id);

        Field field = job.getField();
        if (field != null) {
            // Chỉ set subfields nếu field tồn tại
            field.setSubFields(jobSubfields);
            job.setField(field);
        }
        return jobMapper.mapEntityToResponse(job);
    }

}
