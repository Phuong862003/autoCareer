package com.demo.autocareer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.demo.autocareer.dto.request.ApplyJobDTORequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.exception.ErrorCode;
import com.demo.autocareer.mapper.ApplyJobMapper;
import com.demo.autocareer.model.ApplyJob;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.model.enums.ApplyJobStatus;
import com.demo.autocareer.model.enums.JobStatus;
import com.demo.autocareer.repository.ApplyJobRepository;
import com.demo.autocareer.repository.JobDetailRepository;
import com.demo.autocareer.repository.StudentRepository;
import com.demo.autocareer.service.ApplyJobService;
import com.demo.autocareer.service.storage.FileStorageService;
import com.demo.autocareer.utils.ExceptionUtil;
import com.demo.autocareer.utils.JwtUtil;

@Service
public class ApplyJobServiceImpl implements ApplyJobService{
    @Autowired
    private ApplyJobRepository applyJobRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JobDetailRepository jobDetailRepository;

    @Autowired
    private ApplyJobMapper applyJobMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Student getStudentFromToken() {
        String email = jwtUtil.getCurrentUserEmail(); 
        return studentRepository.findByUserEmail(email)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.STUDENT_NOT_FOUND));
    }

    @Override
    public ApplyJobDTOResponse applyJob(ApplyJobDTORequest request, MultipartFile file){
        Student student = getStudentFromToken();

        Job job = jobDetailRepository.findById(request.getId())
        .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.JOB_NOT_FOUND));

        if(applyJobRepository.existsByStudentAndJob(student, job)){
            throw new RuntimeException("You have already applied for this job.");
        }

        String savePath = fileStorageService.storeFile(file, "job-cv");

        ApplyJob applyJob = applyJobMapper.mapRequestToEntity(request);
        applyJob.setStudent(student);
        applyJob.setJob(job);
        applyJob.setJobStatus(ApplyJobStatus.PENDING);
        applyJob.setAttachment(savePath);
        applyJobRepository.save(applyJob);

        return applyJobMapper.mapEntityToResponse(applyJob);
    }
}
