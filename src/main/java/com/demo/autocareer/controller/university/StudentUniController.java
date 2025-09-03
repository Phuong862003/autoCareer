package com.demo.autocareer.controller.university;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.demo.autocareer.dto.OrganizationDTO;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.BasePageRequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.InternSemesterDTOResponse;
import com.demo.autocareer.dto.response.ResponseData;
import com.demo.autocareer.dto.response.StudentDTOResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.demo.autocareer.service.InternSemesterService;
import com.demo.autocareer.service.StudentService;
import com.demo.autocareer.service.UniversityService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.demo.autocareer.filter.StudentInternFilter;

import org.springframework.web.bind.annotation.PutMapping;

import com.demo.autocareer.dto.request.InternRequestApprovedDTORequest;
import com.demo.autocareer.dto.request.StudentDTORequest;
import com.demo.autocareer.filter.CompanyFilter;




@RestController
@RequestMapping("/api/admin-university")
public class StudentUniController {

    private final UniversityService universityService;

    private final InternSemesterService internSemesterService; 

    public StudentUniController(UniversityService universityService, InternSemesterService internSemesterService) {
        this.universityService = universityService;
        this.internSemesterService = internSemesterService;
    }

    @PostMapping("/upload-student")
    public ResponseData<?> uploadStudents(@RequestParam("file") MultipartFile file) {
        universityService.bulkCreateStudents(file);
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("UPLOAD SUCCESS")
                .data(null)
                .build();
    }

    @GetMapping("/list-student")
    public ResponseData<BasePageResponse<StudentDTOResponse>> getListStudent(
                                    @ModelAttribute StudentInternFilter request,
                                    @ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse<StudentDTOResponse> result = universityService.getStudent(request, pageable);
        return ResponseData.<BasePageResponse<StudentDTOResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("GET STUDENTS SUCCESS")
                .data(result)
                .build();
    }

    @GetMapping("/intern-declare/{id}")
    public ResponseData<?> getDetailInternDeclare(@PathVariable Long id) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("GET STUDENTS SUCCESS")
                .data(universityService.getDetailInternDeclare(id))
                .build();
    }
    
    @PutMapping("/intern-declare/{id}/approved")
    public ResponseData<?> approvedInternRequest(@PathVariable Long id, @RequestBody @Valid InternRequestApprovedDTORequest request) {
       return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("APPROVED SUCCESS")
                .data(universityService.handelAppoved(id, request))
                .build();
    }
    
    @GetMapping("/export-student-list")
    public ResponseEntity<Resource> exportStudentListByFilter(@ModelAttribute StudentInternFilter request){
        ByteArrayInputStream in = universityService.exportStudentListByFilter(request);
        InputStreamResource file = new InputStreamResource(in);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=interns.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    @GetMapping("/list-internSemester")
    public ResponseData<BasePageResponse<InternSemesterDTOResponse>> getListInternSemester(
                                    @ModelAttribute BaseFilterRequest request,
                                    @ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse<InternSemesterDTOResponse> result = internSemesterService.getList(request, pageable);
        return ResponseData.<BasePageResponse<InternSemesterDTOResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("GET STUDENTS SUCCESS")
                .data(result)
                .build();
    }

    @GetMapping("/export-intern-list")
    public ResponseEntity<Resource> exportInternListByFilter(@ModelAttribute BaseFilterRequest request) {
        ByteArrayInputStream in = internSemesterService.exportInternSemesterByFilter(request);
        InputStreamResource file = new InputStreamResource(in);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=interns.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    @GetMapping("/faculty")
    public ResponseData<?> getOrganizationFaculty() {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("GET SUCCESS")
                .data(universityService.GetAllOrganizationFaculty())
                .build();
    }

    @GetMapping("/static")
    public ResponseData<?> getUniStatic() {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("GET SUCCESS")
                .data(universityService.getStudentStatic())
                .build();
    }

    @GetMapping("/student-detail/{id}")
    public ResponseData<?> getStudentDetail(@PathVariable Long id) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("GET SUCCESS")
                .data(universityService.getDetailStudent(id))
                .build();
    }

    @PutMapping("/updateStudent/{id}")
    public ResponseData<?> updateStudent(@PathVariable Long id, @RequestBody @Valid StudentDTORequest request) {
       return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("UPDATE SUCCESS")
                .data(universityService.updateStudent(id, request))
                .build();
    }

    @DeleteMapping("/deleteStudent/{id}")
    public ResponseData<?> deleteStudent(@PathVariable Long id) {
        universityService.deletedStudent(id);
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("DELETE SUCCESS")
                .data(null)
                .build();
    } 

    @GetMapping("/list-company")
    public ResponseData<BasePageResponse<OrganizationDTO>> getListCompany(
                                    @ModelAttribute CompanyFilter request,
                                    @ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse<OrganizationDTO> result = universityService.getCompany(request, pageable);
        return ResponseData.<BasePageResponse<OrganizationDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("GET STUDENTS SUCCESS")
                .data(result)
                .build();
    }
}

