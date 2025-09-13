package com.demo.autocareer.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.SaveJobDTORequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.SaveJobDTOResponse;
import com.demo.autocareer.exception.ErrorCode;
import com.demo.autocareer.mapper.ApplyJobMapper;
import com.demo.autocareer.mapper.InternDeclareRequestMapper;
import com.demo.autocareer.mapper.SaveJobMapper;
import com.demo.autocareer.mapper.StudentMapper;
import com.demo.autocareer.mapper.StudentProMapper;
import com.demo.autocareer.model.ApplyJob;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.SaveJob;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.repository.ApplyJobRepository;
import com.demo.autocareer.repository.DistrictRepository;
import com.demo.autocareer.repository.InternDeclareRequestRepository;
import com.demo.autocareer.repository.JobDetailRepository;
import com.demo.autocareer.repository.OrganizationFacultyRepository;
import com.demo.autocareer.repository.RoleRepository;
import com.demo.autocareer.repository.SaveJobRepository;
import com.demo.autocareer.repository.StudentRepository;
import com.demo.autocareer.repository.SubFieldRepository;
import com.demo.autocareer.repository.UserRepository;
import com.demo.autocareer.service.SaveJobService;
import com.demo.autocareer.service.storage.FileStorageService;
import com.demo.autocareer.specification.BaseSpecification;
import com.demo.autocareer.utils.ExceptionUtil;
import com.demo.autocareer.utils.JwtUtil;
import com.demo.autocareer.utils.PageUtils;

@Service
public class SaveJobServiceImpl implements SaveJobService{
    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private JobDetailRepository jobDetailRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SaveJobRepository saveJobRepository;
    @Autowired
    private SaveJobMapper saveJobMapper;

    private final BaseSpecification<SaveJob> baseSpecification = new BaseSpecification<>();

    @Override
    public Student getStudentFromToken() {
        String email = jwtUtil.getCurrentUserEmail(); 
        return studentRepository.findByUserEmail(email)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.STUDENT_NOT_FOUND));
    }

    @Override
    public SaveJobDTOResponse saveJob(SaveJobDTORequest request){
        Student student = getStudentFromToken();

        Job job = jobDetailRepository.findById(request.getId())
        .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.JOB_NOT_FOUND));

        if(saveJobRepository.existsByStudentAndJob(student, job)){
            throw new RuntimeException("You have already save for this job.");
        }

        SaveJob saveJob = saveJobMapper.mapRequestToEntity(request);
        saveJob.setStudent(student);
        saveJob.setJob(job);
        saveJobRepository.save(saveJob);
        return saveJobMapper.mapEntityToResponse(saveJob);
    }

    @Override
    public BasePageResponse<SaveJobDTOResponse> getList(BaseFilterRequest request, Pageable pageable){
        Student student = getStudentFromToken();
        Specification<SaveJob> spec = baseSpecification
            .build(request, "job.title", null, "createdAt", null, null, null)
            .and((root, query, cb) -> cb.equal(root.get("student"), student));

        Sort sort = baseSpecification.buildSort(request, "job.title");
        Pageable sortPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<SaveJob> page = saveJobRepository.findAll(spec, sortPageable);
        Page<SaveJobDTOResponse> result = page.map(saveJobMapper::mapEntityToResponse);
        return PageUtils.fromPage(result);
    }

    @Override
    public void deleteSaveJob(Long id){
        Student student = getStudentFromToken();

        Job job = jobDetailRepository.findById(id)
        .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.JOB_NOT_FOUND));

        SaveJob saveJob = saveJobRepository.findByStudentAndJob(student, job)
            .orElseThrow(() -> new RuntimeException("Bạn chưa lưu job này"));

        saveJobRepository.delete(saveJob);
    }
}
