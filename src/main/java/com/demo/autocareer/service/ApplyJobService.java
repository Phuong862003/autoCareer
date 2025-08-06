package com.demo.autocareer.service;

import org.springframework.web.multipart.MultipartFile;

import com.demo.autocareer.dto.request.ApplyJobDTORequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.model.Student;

public interface  ApplyJobService {
    Student getStudentFromToken();
    ApplyJobDTOResponse applyJob(ApplyJobDTORequest applyJobDTORequest, MultipartFile file);
}
