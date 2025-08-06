package com.demo.autocareer.controller.portal;


import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.autocareer.dto.OrganizationDTO;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.BasePageRequest;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.ResponseData;
import com.demo.autocareer.service.CompanyService;
import com.demo.autocareer.service.UniversityService;

@RestController
@RequestMapping("/api/")
public class UniController {
   private final UniversityService uniService;
    public UniController(UniversityService uniService) {
        this.uniService = uniService;
    } 

    @GetMapping("list-uni")
    public ResponseData<BasePageResponse<OrganizationDTO>> getUni(@ModelAttribute BaseFilterRequest request, @ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse result = uniService.getAllUni(request, pageable);
        return ResponseData.<BasePageResponse<OrganizationDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("GET UNIVERSITY SUCCESS")
                .data(result)
                .build();
    }
}
