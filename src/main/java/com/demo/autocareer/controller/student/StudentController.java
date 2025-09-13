package com.demo.autocareer.controller.student;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.demo.autocareer.dto.StudentDTO;
import com.demo.autocareer.dto.request.ApplyJobDTORequest;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.BasePageRequest;
import com.demo.autocareer.dto.request.InternDeclareRequestDTORequest;
import com.demo.autocareer.dto.request.SaveJobDTORequest;
import com.demo.autocareer.dto.request.StudentBehaviorDTORequest;
import com.demo.autocareer.dto.request.StudentDTORequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.ResponseData;
import com.demo.autocareer.dto.response.SaveJobDTOResponse;
import com.demo.autocareer.service.ApplyJobService;
import com.demo.autocareer.service.SaveJobService;
import com.demo.autocareer.service.StudentBehaviorService;
import com.demo.autocareer.service.StudentService;
import com.demo.autocareer.service.storage.FileDownloadService;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;

    private final FileDownloadService fileDownloadService;

    private final ApplyJobService applyJobService;

    private final StudentBehaviorService studentBehaviorService;

    private final SaveJobService saveJobService;

    public StudentController(StudentService studentService, FileDownloadService fileDownloadService, ApplyJobService applyJobService, StudentBehaviorService studentBehaviorService, SaveJobService saveJobService) {
        this.studentService = studentService;
        this.fileDownloadService = fileDownloadService;
        this.applyJobService = applyJobService;
        this.studentBehaviorService =  studentBehaviorService;
        this.saveJobService = saveJobService;
    }

    @GetMapping("/profile")
    public ResponseData<?> getProfileStudents() {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(studentService.getProfileById())
                .build();
    }

    @PutMapping("/update")
    public ResponseData<?> updateStudents(@RequestBody StudentDTORequest studentDTO) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(studentService.updateProfile(studentDTO))
                .build();
    } 

    @PostMapping("/upload")
    public ResponseData<?> updateStudents(@RequestParam("file") MultipartFile file) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(studentService.uploadCV(file))
                .build();
    } 

    @PostMapping("apply-job")
    public ResponseData<?> applyJob(@RequestPart("applyJobDTORequest") ApplyJobDTORequest applyJobDTORequest,
        @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(applyJobService.applyJob(applyJobDTORequest, file))
                .build();
    }

    @PostMapping("/internship-declare")
    public ResponseData<?>  declareInternship(@RequestBody InternDeclareRequestDTORequest request) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("Internship declaration submitted successfully")
                .data(studentService.declareIntern(request))
                .build();
    }

    @GetMapping("/list-apply-job")
    public ResponseData<BasePageResponse<ApplyJobDTOResponse>> getApplyJob(
                                    @ModelAttribute BaseFilterRequest baseFilterRequest,
                                    @ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse<ApplyJobDTOResponse> result = studentService.getJobApply(baseFilterRequest, pageable);
        return ResponseData.<BasePageResponse<ApplyJobDTOResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("GET APPLY JOB SUCCESS")
                .data(result)
                .build();
    }

    @PostMapping("/behavior")
    public ResponseData<?>  sendBehavior(@RequestBody StudentBehaviorDTORequest request) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("successfully")
                .data(studentBehaviorService.recordBehavior(request))
                .build();
    }

    @PostMapping("/saveJob")
    public ResponseData<?>  saveJob(@RequestBody SaveJobDTORequest request) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("successfully")
                .data(saveJobService.saveJob(request))
                .build();
    }

    @GetMapping("/list-saveJob")
    public ResponseData<BasePageResponse<SaveJobDTOResponse>> getListSaveJob(
                                    @ModelAttribute BaseFilterRequest baseFilterRequest,
                                    @ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse<SaveJobDTOResponse> result = saveJobService.getList(baseFilterRequest, pageable);
        return ResponseData.<BasePageResponse<SaveJobDTOResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("GET SAVE JOB SUCCESS")
                .data(result)
                .build();
    }

    @DeleteMapping("/unSave/{id}")
    public ResponseData<?> unSave(@PathVariable Long id) {
        saveJobService.deleteSaveJob(id);
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("UNSAVE SUCCESS")
                .data(null)
                .build();
    } 
}


