package com.demo.autocareer.controller.portal;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.demo.autocareer.dto.OrganizationDTO;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.BasePageRequest;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.JobDTOResponse;
import com.demo.autocareer.dto.response.ResponseData;
import com.demo.autocareer.service.CompanyService;
import com.demo.autocareer.service.JobDetailService;



@RestController
@RequestMapping("/api")
public class CompanyController {
    private final CompanyService companyService;
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }
    @GetMapping("/list-company")
    public ResponseData<BasePageResponse<OrganizationDTO>> getCompany(@ModelAttribute BaseFilterRequest request, @ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse<OrganizationDTO> result = companyService.getCompany(request, pageable);
        return ResponseData.<BasePageResponse<OrganizationDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("GET COMPANY SUCCESS")
                .data(result)
                .build();
    }

    @GetMapping("/company-job/{id}")
    public ResponseData<BasePageResponse<JobDTOResponse>> getCompanyJob(@PathVariable Long id,@ModelAttribute BaseFilterRequest request, @ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse<JobDTOResponse> result = companyService.getJobCompany(id, request, pageable);
        return ResponseData.<BasePageResponse<JobDTOResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("GET COMPANY JOB SUCCESS")
                .data(result)
                .build();
    }
    
}
