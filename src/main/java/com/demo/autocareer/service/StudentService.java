package com.demo.autocareer.service;

import java.io.InputStream;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.demo.autocareer.dto.StudentDTO;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.InternDeclareRequestDTORequest;
import com.demo.autocareer.dto.request.StudentDTORequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.InternDeclareRequestDTOResponse;
import com.demo.autocareer.dto.response.JobDTOResponse;
import com.demo.autocareer.dto.response.StudentDTOResponse;
import com.demo.autocareer.filter.StudentInternFilter;
import com.demo.autocareer.model.Student;

@Service
public interface StudentService {
    StudentDTOResponse getProfileById();
    Student getStudentFromToken();
    StudentDTOResponse updateProfile(StudentDTORequest studentDTO);
    String uploadCV(MultipartFile file);
    InternDeclareRequestDTOResponse declareIntern(InternDeclareRequestDTORequest request);
    BasePageResponse<ApplyJobDTOResponse> getJobApply(BaseFilterRequest request, Pageable pageable);
}
