package com.demo.autocareer.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.demo.autocareer.dto.OrganizationDTO;
import com.demo.autocareer.dto.RoleDTO;
import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.OrganizationDTORequest;
import com.demo.autocareer.dto.request.UserDTORequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.UserDTOResponse;
import com.demo.autocareer.dto.response.UserStaticDTOResponse;
import com.demo.autocareer.model.User;

public interface AdminService {
    BasePageResponse<UserDTOResponse> getListUser(BaseFilterRequest request, Pageable pageable);
    UserDTOResponse updateUser(Long id, UserDTORequest request);
    void deleteUser(Long id);
    List<RoleDTO> findAllRole();
    UserStaticDTOResponse getUserStatistics();
    BasePageResponse<OrganizationDTO> getListOrg(BaseFilterRequest request, Pageable pageable);
    OrganizationDTO getDetailOrg(Long id);
    OrganizationDTO updateOrg(OrganizationDTORequest request);
    void deleteOrg(Long id);
    User getFromToken();
    UserDTOResponse getProfileById();
}
