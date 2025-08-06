package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.RoleDTO;
import com.demo.autocareer.model.Role;

@Mapper(componentModel = "spring")
public interface  RoleMapper {
    RoleDTO toDo(Role role);
    Role toEntity(RoleDTO dto);
}
