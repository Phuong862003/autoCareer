package com.demo.autocareer.controller.company;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.autocareer.dto.response.ResponseData;
import com.demo.autocareer.service.CompanyService;
import com.demo.autocareer.service.InternshipAssignmentService;
import com.demo.autocareer.service.InternshipRequestService;
import com.demo.autocareer.service.UniversityService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.BasePageRequest;
import com.demo.autocareer.dto.request.InternshipApprovedDTORequest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.demo.autocareer.dto.request.InternshipRequestDTORequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.InternRequestFileDTOResponse;
import com.demo.autocareer.dto.response.InternshipAssignDTOResponse;
import com.demo.autocareer.dto.response.InternshipRequestDTOResponse;
import com.demo.autocareer.filter.InternshipRequestFilter;



@RestController
@RequestMapping("/api/admin-company")
public class InternShipRequestController {
    private final CompanyService companyService;
    private final InternshipAssignmentService internshipAssignmentService;
    private final InternshipRequestService internshipRequestService;

    public InternShipRequestController(CompanyService companyService, InternshipAssignmentService internshipAssignmentService, InternshipRequestService internshipRequestService) {
        this.companyService = companyService;
        this.internshipAssignmentService = internshipAssignmentService;
        this.internshipRequestService = internshipRequestService;
    }

    @PutMapping("/intern-request/approved/{id}")
    public ResponseData<?> approvedInternshipRequest(@PathVariable Long id, @RequestBody @Valid InternshipApprovedDTORequest request) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("APPROVED SUCCESS")
                .data(companyService.handelRequest(id, request))
                .build();
    }

    @GetMapping("/list-internship")
    public ResponseData<BasePageResponse<InternshipRequestDTOResponse>> getListInternship(@ModelAttribute InternshipRequestFilter request,@ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse<InternshipRequestDTOResponse> result = companyService.getInternshipRequest(request, pageable);
        return ResponseData.<BasePageResponse<InternshipRequestDTOResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("GET LIST INTERNSHIP SUCCESS")
                .data(result)
                .build();
    }
    
    @GetMapping("/list-internshipAssign/{id}")
    public ResponseData<BasePageResponse<InternshipAssignDTOResponse>> getListInternshipAssign(@PathVariable Long id,@ModelAttribute BaseFilterRequest request,@ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse<InternshipAssignDTOResponse> result = internshipAssignmentService.getListInternship(id, request, pageable);
        return ResponseData.<BasePageResponse<InternshipAssignDTOResponse>>builder()
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
                .data(internshipRequestService.getInternRequestStatic())
                .build();
    }
    
    @GetMapping("/internRequest-detail/{id}")
    public ResponseData<?> getInternRequestDetail(@PathVariable Long id) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(internshipRequestService.getDetail(id))
                .build();
    }

    @DeleteMapping("/deleted-internRequest/{id}")
    public ResponseData<?> deletedInternRequest(@PathVariable Long id) {
        internshipRequestService.deletedInternRequest(id);
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("DELETE SUCCESS")
                .data(null)
                .build();  
    } 

    @GetMapping("/list-internRequest")
    public ResponseData<BasePageResponse<InternRequestFileDTOResponse>> getListInternRequest(@ModelAttribute BaseFilterRequest baseFilterRequest,@ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse<InternRequestFileDTOResponse> result = internshipAssignmentService.getListInternRequestFile(baseFilterRequest, pageable);
        return ResponseData.<BasePageResponse<InternRequestFileDTOResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("GET LIST INTERNSHIP SUCCESS")
                .data(result)
                .build();
    }
}
