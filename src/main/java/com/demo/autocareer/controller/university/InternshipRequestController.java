package com.demo.autocareer.controller.university;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.BasePageRequest;
import com.demo.autocareer.dto.request.InternshipRequestDTORequest;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.InternRequestFileDTOResponse;
import com.demo.autocareer.dto.response.InternshipAssignDTOResponse;
import com.demo.autocareer.dto.response.ResponseData;
import com.demo.autocareer.service.InternshipAssignmentService;
import com.demo.autocareer.service.InternshipRequestService;


@RestController
@RequestMapping("/api/admin-university")
public class InternshipRequestController {
    private final InternshipRequestService internshipRequestService;
    private final InternshipAssignmentService internshipAssignmentService;
    public InternshipRequestController(InternshipRequestService internshipRequestService, InternshipAssignmentService internshipAssignmentService) {
        this.internshipRequestService = internshipRequestService;
        this.internshipAssignmentService = internshipAssignmentService;
    }

    @PostMapping("/create-internship-request")
    public ResponseData<?> createInternshipRequest(@RequestBody InternshipRequestDTORequest request) {
        System.out.println(">>> RECEIVED: " + request); // in ra toàn bộ object
        System.out.println(">>> companyId: " + request.getCompanyId());
        System.out.println(">>> title: " + request.getTitle());

        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("CREATE SUCCESS")
                .data(internshipRequestService.createInternshipRequest(request))
                .build();
    }

    @PostMapping("/send-internship")
    public ResponseData<?> sendInternship(@RequestParam("file") MultipartFile file,
                                            @RequestParam("internshipRequestId") Long id){
        internshipAssignmentService.sendInternship(file, id);
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("UPLOAD SUCCESS")
                .data(null)
                .build();
    }
    
    @GetMapping("/internRequest-detail/{id}")
    public ResponseData<?> getDetail(@PathVariable Long id){
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(internshipRequestService.getDetailInternRequest(id))
                .build();
    }

    @GetMapping("/list-intern-request")
    public ResponseData<BasePageResponse<InternRequestFileDTOResponse>> getListInternRequest(@ModelAttribute BaseFilterRequest baseFilterRequest,@ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse<InternRequestFileDTOResponse> result = internshipAssignmentService.getUniListInternRequestFile(baseFilterRequest, pageable);
        return ResponseData.<BasePageResponse<InternRequestFileDTOResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("GET LIST INTERNSHIP SUCCESS")
                .data(result)
                .build();
    }

    @GetMapping("/internRequest-stats")
    public ResponseData<?> getInternRequestStats() {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(internshipRequestService.getUniInternRequestStatic())
                .build();
    }

    @GetMapping("/list-internshipAssign/{id}")
    public ResponseData<BasePageResponse<InternshipAssignDTOResponse>> getUniListInternshipAssign(@PathVariable Long id,@ModelAttribute BaseFilterRequest request,@ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse<InternshipAssignDTOResponse> result = internshipAssignmentService.getUniListInternship(id, request, pageable);
        return ResponseData.<BasePageResponse<InternshipAssignDTOResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("GET LIST INTERNSHIP SUCCESS")
                .data(result)
                .build();
    }
}