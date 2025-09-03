package com.demo.autocareer.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.demo.autocareer.dto.InternshipAssignmentExcelDTO;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.InternRequestFileDTOResponse;
import com.demo.autocareer.dto.response.InternshipAssignDTOResponse;
import com.demo.autocareer.excel.ExcelStudentParser;
import com.demo.autocareer.exception.ErrorCode;
import com.demo.autocareer.mapper.InternRequestFileMapper;
import com.demo.autocareer.mapper.InternshipAssignMapper;
import com.demo.autocareer.model.ApplyJob;
import com.demo.autocareer.model.InternshipAssignment;
import com.demo.autocareer.model.InternshipRequest;
import com.demo.autocareer.model.InternshipRequestFile;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.model.enums.StatusAssign;
import com.demo.autocareer.model.enums.StatusRequest;
import com.demo.autocareer.model.enums.StatusRequestFile;
import com.demo.autocareer.repository.InternshipAssignmentRepository;
import com.demo.autocareer.repository.InternshipRequestFileRepository;
import com.demo.autocareer.repository.InternshipRequestRepository;
import com.demo.autocareer.repository.OrganizationRepository;
import com.demo.autocareer.repository.RoleRepository;
import com.demo.autocareer.repository.StudentRepository;
import com.demo.autocareer.repository.UserRepository;
import com.demo.autocareer.service.InternshipAssignmentService;
import com.demo.autocareer.service.storage.FileStorageService;
import com.demo.autocareer.specification.BaseSpecification;
import com.demo.autocareer.utils.ExceptionUtil;
import com.demo.autocareer.utils.JwtUtil;
import com.demo.autocareer.utils.PageUtils;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class InternshipAssignServiceImpl implements InternshipAssignmentService{
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private InternshipRequestRepository internshipRequestRepository;
    @Autowired
    private InternshipAssignmentRepository internshipAssignmentRepository;
    @Autowired
    private ExcelStudentParser excelStudentParser;
    @Autowired
    private InternshipAssignMapper internshipAssignMapper;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private InternshipRequestFileRepository internshipRequestFileRepository;
    @Autowired
    private InternRequestFileMapper internRequestFileMapper;

    private final BaseSpecification<InternshipAssignment> baseSpecification = new BaseSpecification<>();
    private final BaseSpecification<InternshipRequestFile> baseSpecificationInternRequest = new BaseSpecification<>();

    @Override
    public Organization getUniFromToken() {
        String email = jwtUtil.getCurrentUserEmail(); 
        return organizationRepository.findByUserEmail(email)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.COMPANY_NOT_FOUND));
    }

    @Override
    public void sendInternship(MultipartFile file, Long internshipId){
        InternshipRequest internshipRequest = internshipRequestRepository.findById(internshipId)
                .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.INTERN_REQUEST_NOT_FOUND));
        
        if (internshipRequest.getStatusRequest() != StatusRequest.APPROVED){
            throw ExceptionUtil.fromErrorCode(ErrorCode.INTERN_REQUEST_APPROVED);
        }

        String folder = "internship_request" + internshipId;
        String filePath = fileStorageService.storeFile(file, folder);
        InternshipRequestFile requestFile = new InternshipRequestFile();
        requestFile.setOriginalFileName(file.getOriginalFilename());
        requestFile.setFilePath(filePath);
        requestFile.setInternshipRequest(internshipRequest);
        requestFile.setStatus(StatusRequestFile.PENDING);
        internshipRequestFileRepository.save(requestFile);

        List<InternshipAssignmentExcelDTO> studentList = excelStudentParser.parse(file);

        for(InternshipAssignmentExcelDTO dto : studentList){
            Student student = studentRepository.findByStudentCode(dto.getStudentCode())
                    .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.STUDENT_NOT_FOUND));
            
            InternshipAssignment assignment = new InternshipAssignment();
            assignment.setInternshipRequest(internshipRequest);
            assignment.setInternRequestFile(requestFile);
            assignment.setStudent(student);
            assignment.setName(dto.getName());
            assignment.setStudentCode(dto.getStudentCode());
            assignment.setEmail(dto.getEmail());
            assignment.setPhoneNumber(dto.getPhoneNumber());
            assignment.setDob(dto.getDob());
            assignment.setGraduatedYear(dto.getGraduatedYear());
            assignment.setFacultyName(dto.getFacultyName());
            assignment.setSkill(dto.getSkill());
            assignment.setGender(dto.getGender());
            assignment.setStatus(StatusAssign.WAITING);
            internshipAssignmentRepository.save(assignment);
        }
    }

    @Override
    public BasePageResponse<InternshipAssignDTOResponse> getListInternship(Long internshipRequestId, BaseFilterRequest request, Pageable pageable){
        InternshipRequest internshipRequest = internshipRequestRepository.findById(internshipRequestId)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.INTERN_REQUEST_NOT_FOUND));

        Organization comp = getUniFromToken();
        Specification<InternshipAssignment> spec = baseSpecification
            .build(request, "name", "status", null, null, null, null)
            .and((root, query, cb) -> cb.and(
                    cb.equal(root.get("internshipRequest").get("company"), comp),
                    cb.equal(root.get("internshipRequest").get("id"), internshipRequestId)
            ));

        Sort sort = baseSpecification.buildSort(request, "name");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<InternshipAssignment> page = internshipAssignmentRepository.findAll(spec, sortPageable);
        Page<InternshipAssignDTOResponse> result = page.map(internshipAssignMapper::mapEntityToResponse);
        return PageUtils.fromPage(result);
    }

    @Override
    public BasePageResponse<InternRequestFileDTOResponse> getListInternRequestFile(BaseFilterRequest request, Pageable pageable) {
        Organization comp = getUniFromToken();

        Specification<InternshipRequestFile> spec = baseSpecificationInternRequest
                .build(request, "internshipRequest.university.organizationName", "status", null, null, null, null)
                .and((root, query, cb) -> cb.equal(root.get("internshipRequest").get("company"), comp));

        Sort sort = baseSpecificationInternRequest.buildSort(request, "internshipRequest.university.organizationName");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<InternshipRequestFile> page = internshipRequestFileRepository.findAll(spec, sortPageable);
        Page<InternRequestFileDTOResponse> result = page.map(internRequestFileMapper::toDto);
        return PageUtils.fromPage(result);
    }

    @Override
    public BasePageResponse<InternRequestFileDTOResponse> getUniListInternRequestFile(BaseFilterRequest request, Pageable pageable) {
        Organization uni = getUniFromToken();

        Specification<InternshipRequestFile> spec = baseSpecificationInternRequest
                .build(request, "internshipRequest.company.organizationName", "status", null, null, null, null)
                .and((root, query, cb) -> cb.equal(root.get("internshipRequest").get("university"), uni));

        Sort sort = baseSpecificationInternRequest.buildSort(request, "internshipRequest.company.organizationName");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<InternshipRequestFile> page = internshipRequestFileRepository.findAll(spec, sortPageable);
        Page<InternRequestFileDTOResponse> result = page.map(internRequestFileMapper::toDto);
        return PageUtils.fromPage(result);
    }

    @Override
    public BasePageResponse<InternshipAssignDTOResponse> getUniListInternship(Long internshipRequestFileId, BaseFilterRequest request, Pageable pageable){
        // InternshipRequest internshipRequest = internshipRequestRepository.findById(internshipRequestId)
        //     .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.INTERN_REQUEST_NOT_FOUND));
        
        InternshipRequestFile intern = internshipRequestFileRepository.findById(internshipRequestFileId)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.INTERN_REQUEST_NOT_FOUND));

        Organization uni = getUniFromToken();
        Specification<InternshipAssignment> spec = baseSpecification
            .build(request, "name", "status", null, null, null, null)
            .and((root, query, cb) -> cb.and(
                    cb.equal(root.get("internshipRequest").get("university"), uni),
                    cb.equal(root.get("internRequestFile").get("id"), internshipRequestFileId)
            ));

        Sort sort = baseSpecification.buildSort(request, "name");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<InternshipAssignment> page = internshipAssignmentRepository.findAll(spec, sortPageable);
        Page<InternshipAssignDTOResponse> result = page.map(internshipAssignMapper::mapEntityToResponse);
        return PageUtils.fromPage(result);
    }


}
