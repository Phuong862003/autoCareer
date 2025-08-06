package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.OrganizationDTO;
import com.demo.autocareer.dto.request.OrganizationDTORequest;
import com.demo.autocareer.model.Organization;

@Mapper(componentModel = "spring")
public interface OrganizationMapper extends EntityMapper<OrganizationDTORequest, Organization, OrganizationDTO> {
    @Override
    OrganizationDTO mapEntityToResponse(Organization organization);
    // OrganizationDTO toDto(Organization organization);

    default Organization fromId(Long id) {
        if (id == null) return null;
        Organization organization = new Organization();
        organization.setId(id);
        return organization;
    }
}
