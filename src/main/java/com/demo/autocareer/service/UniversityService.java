package com.demo.autocareer.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.demo.autocareer.dto.OrganizationDTO;
import com.demo.autocareer.dto.OrganizationFacultyDTO;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.InternRequestApprovedDTORequest;
import com.demo.autocareer.dto.request.StudentDTORequest;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.InternDeclareRequestDTOResponse;
import com.demo.autocareer.dto.response.InternRequestApprovedDTOReponse;
import com.demo.autocareer.dto.response.StudentDTOResponse;
import com.demo.autocareer.dto.response.StudentStaticDTOResponse;
import com.demo.autocareer.filter.StudentInternFilter;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.OrganizationFaculty;
import com.demo.autocareer.filter.CompanyFilter;

public interface  UniversityService {
    Organization getUniFromToken();
    void bulkCreateStudents(MultipartFile multipartFile);
    BasePageResponse<StudentDTOResponse> getStudent(StudentInternFilter request, Pageable pageable);
    InternDeclareRequestDTOResponse getDetailInternDeclare(Long studentId);
    InternRequestApprovedDTOReponse handelAppoved(Long id, InternRequestApprovedDTORequest request);
    BasePageResponse<OrganizationDTO> getAllUni(BaseFilterRequest request, Pageable pageable);
    ByteArrayInputStream exportStudentListByFilter(StudentInternFilter request);
    List<OrganizationFacultyDTO> GetAllOrganizationFaculty();
    StudentStaticDTOResponse getStudentStatic();
    StudentDTOResponse getDetailStudent(Long studentId);
    StudentDTOResponse updateStudent(Long id, StudentDTORequest studentDTO);
    void deletedStudent(Long id);
    BasePageResponse<OrganizationDTO> getCompany(CompanyFilter request, Pageable pageable);
}
