package com.demo.autocareer.controller.university;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.BasePageRequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.ResponseData;
import com.demo.autocareer.dto.response.StudentDTOResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.demo.autocareer.service.StudentService;
import com.demo.autocareer.service.UniversityService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.demo.autocareer.filter.StudentInternFilter;

import org.springframework.web.bind.annotation.PutMapping;

import com.demo.autocareer.dto.request.InternRequestApprovedDTORequest;




@RestController
@RequestMapping("/api/admin-university")
public class StudentUniController {

    private final UniversityService universityService;

    public StudentUniController(UniversityService universityService) {
        this.universityService = universityService;
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
    public ResponseData<?> exportStudentListByFilter(@ModelAttribute StudentInternFilter request){
        universityService.exportStudentListByFilter(request);
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("EXPORT SUCCESS")
                .data(null)
                .build();
    }
}

