package com.demo.autocareer.service.impl;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.InternSemesterDTOResponse;
import com.demo.autocareer.dto.response.JobDTOResponse;
import com.demo.autocareer.dto.response.StudentDTOResponse;
import com.demo.autocareer.excel.WriteStudentExcel;
import com.demo.autocareer.exception.ErrorCode;
import com.demo.autocareer.mapper.InternSemesterMapper;
import com.demo.autocareer.model.ApplyJob;
import com.demo.autocareer.model.InternshipSemester;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.Semester;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.model.enums.ApplyJobStatus;
import com.demo.autocareer.model.enums.StatusInternSemester;
import com.demo.autocareer.repository.InternshipSemesterRepository;
import com.demo.autocareer.repository.OrganizationRepository;
import com.demo.autocareer.service.InternSemesterService;
import com.demo.autocareer.specification.BaseSpecification;
import com.demo.autocareer.specification.InternSemesterSpecification;
import com.demo.autocareer.utils.ExceptionUtil;
import com.demo.autocareer.utils.JwtUtil;
import com.demo.autocareer.utils.PageUtils;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

@Service
public class InternSemesterServiceImpl implements InternSemesterService{
    @Autowired
    private InternshipSemesterRepository internSemesterRepository;

    @Autowired
    private InternSemesterMapper internSemesterMapper;

    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private WriteStudentExcel writeStudentExcel;

    private final BaseSpecification<InternshipSemester> baseSpecification = new BaseSpecification<>();
    private final InternSemesterSpecification internSemesterSpecification = new InternSemesterSpecification();

    @Override
    public Organization getOrgFromToken() {
        String email = jwtUtil.getCurrentUserEmail(); 
        return organizationRepository.findByUserEmail(email)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.COMPANY_NOT_FOUND));
    }

    @Override
    public BasePageResponse<InternSemesterDTOResponse> getList(BaseFilterRequest request,Pageable pageable){
        Organization company = getOrgFromToken();
        if(request.getFilters() != null){
            request.setFilters(new HashMap<>(request.getFilters()));
            request.getFilters().remove("status");
        }
        Specification<InternshipSemester> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<InternshipSemester, Student> studentJoin = root.join("student", JoinType.LEFT);
            Join<InternshipSemester, Semester> semesterJoin = root.join("semester", JoinType.LEFT);
            
            if (StringUtils.hasText(request.getKeyword())){
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(studentJoin.get("name")), keyword),
                    cb.like(cb.lower(studentJoin.get("studentCode")), keyword),
                    cb.like(cb.lower(studentJoin.get("email")), keyword)
                ));
            }

            if (request.getEnumValue() != null && !request.getEnumValue().isBlank()) {
                predicates.add(cb.equal(root.get("status"), StatusInternSemester.valueOf(request.getEnumValue())));
            }

            if (request.getFilters() != null) {
                String semesterCode = request.getFilters().get("semesterCode");
                if (StringUtils.hasText(semesterCode)) {
                    predicates.add(cb.equal(cb.lower(semesterJoin.get("code")), semesterCode.toLowerCase()));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Sort sort = baseSpecification.buildSort(request, "student.name");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<InternshipSemester> page = internSemesterRepository.findAll(spec, sortPageable);
        Page<InternSemesterDTOResponse> result = page.map(internSemesterMapper::mapEntityToResponse);
        return PageUtils.fromPage(result);
    }

    @Override
    public ByteArrayInputStream exportInternSemesterByFilter(BaseFilterRequest request) {
        Organization company = getOrgFromToken();
        Specification<InternshipSemester> spec = internSemesterSpecification.buildSpec(request, company);
        List<InternshipSemester> interns = internSemesterRepository.findAll(spec);

        List<InternSemesterDTOResponse> dto = interns.stream()
                .map(internSemesterMapper::mapEntityToResponse)
                .collect(Collectors.toList());

        return writeStudentExcel.writeInternSemesterExcel(dto);
    }

}
