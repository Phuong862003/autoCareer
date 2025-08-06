package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.OrganizationFacultyDTO;
import com.demo.autocareer.model.OrganizationFaculty;

@Mapper(componentModel = "spring", uses={OrganizationMapper.class, FacultyMapper.class})
public interface  OrganizationFacultyMapper {
    OrganizationFacultyDTO toDto(OrganizationFaculty organizationFaculty);
}
