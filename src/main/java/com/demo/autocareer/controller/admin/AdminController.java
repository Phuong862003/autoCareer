package com.demo.autocareer.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.autocareer.dto.OrganizationDTO;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.BasePageRequest;
import com.demo.autocareer.dto.request.OrganizationDTORequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.ResponseData;
import com.demo.autocareer.dto.response.UserDTOResponse;
import com.demo.autocareer.dto.response.UserStaticDTOResponse;
import com.demo.autocareer.service.AdminService;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import com.demo.autocareer.dto.request.UserDTORequest;



@RestController
@RequestMapping("/api/admin/")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService){
        this.adminService = adminService;
    }

    @GetMapping("user")
    public ResponseData<BasePageResponse<UserDTOResponse>> getUser(
                                    @ModelAttribute BaseFilterRequest baseFilterRequest,
                                    @ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse<UserDTOResponse> result = adminService.getListUser(baseFilterRequest, pageable);
        return ResponseData.<BasePageResponse<UserDTOResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("GET USER SUCCESS")
                .data(result)
                .build();
    }

    @PutMapping("user/update/{id}")
    public ResponseData<?> updateUser(@PathVariable Long id, @RequestBody UserDTORequest request) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("UPDATE SUCCESS")
                .data(adminService.updateUser(id, request))
                .build();
    }
    
    @DeleteMapping("user/delete/{id}")
    public ResponseData<?> deleteJob(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("DELETE SUCCESS")
                .data(null)
                .build();
    }

    @GetMapping("/role")
    public ResponseData<?> getRole() {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("GET  SUCCESS")
                .data(adminService.findAllRole())
                .build();
    }
    
    @GetMapping("/user/static")
    public ResponseData<?> getCompanyStats() {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(adminService.getUserStatistics())
                .build();
    }

    @GetMapping("org")
    public ResponseData<BasePageResponse<OrganizationDTO>> getOrg(
                                    @ModelAttribute BaseFilterRequest baseFilterRequest,
                                    @ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse<OrganizationDTO> result = adminService.getListOrg(baseFilterRequest, pageable);
        return ResponseData.<BasePageResponse<OrganizationDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("GET ORG SUCCESS")
                .data(result)
                .build();
    }

    @GetMapping("org-detail/{id}")
    public ResponseData<?> getOrgDetail(@PathVariable Long id) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(adminService.getDetailOrg(id))
                .build();
    }

    @PutMapping("org/update")
    public ResponseData<?> updateUser(@RequestBody OrganizationDTORequest request) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("UPDATE SUCCESS")
                .data(adminService.updateOrg(request))
                .build();
    }

    @DeleteMapping("org/delete/{id}")
    public ResponseData<?> deleteOrg(@PathVariable Long id) {
        adminService.deleteOrg(id);
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("DELETE SUCCESS")
                .data(null)
                .build();
    }

    @GetMapping("profile")
    public ResponseData<?> getProfile() {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("SUCCESS")
                .data(adminService.getProfileById())
                .build();
    }
}
