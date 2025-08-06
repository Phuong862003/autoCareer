package com.demo.autocareer.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.demo.autocareer.dto.StudentDTO;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.InternDeclareRequestDTOResponse;
import com.demo.autocareer.dto.response.StudentDTOResponse;
import com.demo.autocareer.exception.ErrorCode;
import com.demo.autocareer.model.ApplyJob;
import com.demo.autocareer.model.District;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.OrganizationFaculty;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.model.SubField;
import com.demo.autocareer.model.enums.StatusIntern;
import com.demo.autocareer.repository.DistrictRepository;
import com.demo.autocareer.repository.InternDeclareRequestRepository;
import com.demo.autocareer.repository.OrganizationFacultyRepository;
import com.demo.autocareer.repository.RoleRepository;
import com.demo.autocareer.repository.StudentRepository;
import com.demo.autocareer.repository.SubFieldRepository;
import com.demo.autocareer.repository.UserRepository;
import com.demo.autocareer.service.StudentService;
import com.demo.autocareer.service.storage.FileStorageService;
import com.demo.autocareer.specification.BaseSpecification;
import com.demo.autocareer.utils.ExceptionUtil;

import com.demo.autocareer.utils.JwtUtil;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.InternDeclareRequestDTORequest;
import com.demo.autocareer.mapper.ApplyJobMapper;
import com.demo.autocareer.mapper.InternDeclareRequestMapper;
import com.demo.autocareer.mapper.StudentMapper;
import com.demo.autocareer.model.InternDeclareRequest;
import com.demo.autocareer.repository.ApplyJobRepository;
import com.demo.autocareer.repository.JobDetailRepository;
import com.demo.autocareer.utils.PageUtils;


@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JobDetailRepository jobDetailRepository;
    @Autowired
    private InternDeclareRequestRepository internDeclareRepository;
    @Autowired
    private OrganizationFacultyRepository organizationFacultyRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private SubFieldRepository subFieldRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ApplyJobRepository applyJobRepository;
    @Autowired
    private ApplyJobMapper applyJobMapper;
    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private InternDeclareRequestMapper internDeclareMapper;

    private final BaseSpecification<ApplyJob> baseSpecification = new BaseSpecification<>();

    @Override
    public Student getStudentFromToken() {
        String email = jwtUtil.getCurrentUserEmail(); 
        return studentRepository.findByUserEmail(email)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.STUDENT_NOT_FOUND));
    }


    @Override
    public StudentDTOResponse getProfileById(Long id){
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.STUDENT_NOT_FOUND));
        StudentDTOResponse response = studentMapper.mapEntityToResponse(student);
        return response;

    }

    @Override
    public StudentDTOResponse updateProfile(StudentDTO studentDTO){
        Student student = getStudentFromToken();
        
        studentMapper.partialUpdate(student, studentDTO);

        if(studentDTO.getDistrictId() != null){
            District district = districtRepository.findById(studentDTO.getDistrictId())
                    .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.DISTRICT_NOT_FOUND));
            student.setDistrict(district);
        }else{
            student.setDistrict(null);
        }

        if (studentDTO.getOrganizationFacultyId() != null) {
            OrganizationFaculty of = organizationFacultyRepository.findById(studentDTO.getOrganizationFacultyId())
                .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.ORG_FACULTY_NOT_FOUND));
            student.setOrganizationFaculty(of);
        } else {
            student.setOrganizationFaculty(null);
        }

        if (studentDTO.getSubFieldId() != null) {
            SubField subField = subFieldRepository.findById(studentDTO.getSubFieldId())
                .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.SUBFIELD_NOT_FOUND));
            student.setSubField(subField);
        } else {
            student.setSubField(null);
        }

        studentRepository.save(student);
        return studentMapper.mapEntityToResponse(student);
    }

    @Override
    public String uploadCV(MultipartFile file){
        Student student = getStudentFromToken();

        String savePath = fileStorageService.storeFile(file, "cv");
        student.setAttachment(savePath);
        studentRepository.save(student);
        return savePath;
    }

    @Override
    public InternDeclareRequestDTOResponse declareIntern(InternDeclareRequestDTORequest dto){
        Student student = getStudentFromToken();
        Job job = jobDetailRepository.findById(dto.getJobDetailId())
            .orElseThrow(() -> new RuntimeException("Job not found"));

        InternDeclareRequest entity = internDeclareMapper.mapRequestToEntity(dto);
        entity.setJobDetail(job);
        entity.setStudent(student);
        entity.setStatusIntern(StatusIntern.WAITING);
        internDeclareRepository.save(entity);
        return internDeclareMapper.mapEntityToResponse(entity);
    }

    // @Override
    // public BasePageResponse<ApplyJobDTOResponse> getJobApply(Pageable pageable){
    //     Student student = getStudentFromToken();
    //     Page<ApplyJob> page = applyJobRepository.findByStudent(student, pageable);
    //     Page<ApplyJobDTOResponse> applyJob = page.map(applyJobMapper::mapEntityToResponse);

    //     return PageUtils.fromPage(applyJob);
    // }

    @Override
    public BasePageResponse<ApplyJobDTOResponse> getJobApply(BaseFilterRequest request, Pageable pageable){
        Student student = getStudentFromToken();
        Specification<ApplyJob> spec = baseSpecification
            .build(request, "job.title", "ApplyJobStatus", "createdAt", null, null)
            .and((root, query, cb) -> cb.equal(root.get("student"), student));

        Sort sort = baseSpecification.buildSort(request, "job.title");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<ApplyJob> page = applyJobRepository.findAll(spec, sortPageable);
        Page<ApplyJobDTOResponse> result = page.map(applyJobMapper::mapEntityToResponse);
        return PageUtils.fromPage(result);
    }
}
