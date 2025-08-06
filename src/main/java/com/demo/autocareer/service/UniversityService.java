package com.demo.autocareer.service;

import java.io.IOException;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.demo.autocareer.dto.OrganizationDTO;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.InternRequestApprovedDTORequest;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.InternDeclareRequestDTOResponse;
import com.demo.autocareer.dto.response.InternRequestApprovedDTOReponse;
import com.demo.autocareer.dto.response.StudentDTOResponse;
import com.demo.autocareer.filter.StudentInternFilter;
import com.demo.autocareer.model.Organization;

public interface  UniversityService {
    Organization getUniFromToken();
    void bulkCreateStudents(MultipartFile multipartFile);
    BasePageResponse<StudentDTOResponse> getStudent(StudentInternFilter request, Pageable pageable);
    InternDeclareRequestDTOResponse getDetailInternDeclare(Long studentId);
    InternRequestApprovedDTOReponse handelAppoved(Long id, InternRequestApprovedDTORequest request);
    BasePageResponse<OrganizationDTO> getAllUni(BaseFilterRequest request, Pageable pageable);
    String exportStudentListByFilter(StudentInternFilter request);
}
