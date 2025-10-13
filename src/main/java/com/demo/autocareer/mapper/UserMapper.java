package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.demo.autocareer.dto.request.UserDTORequest;
import com.demo.autocareer.dto.response.UserDTOResponse;
import com.demo.autocareer.model.User;

@Mapper(componentModel = "spring", uses={RoleMapper.class, DistrictMapper.class})
public interface  UserMapper extends EntityMapper<UserDTORequest, User, UserDTOResponse>{

    @Override
    UserDTOResponse mapEntityToResponse(User dto);

    @Override
    void partialUpdate(@MappingTarget User user, UserDTORequest request);
}
