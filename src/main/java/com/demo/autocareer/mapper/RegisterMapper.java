package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.demo.autocareer.dto.request.RegisterDTORequest;
import com.demo.autocareer.dto.response.RegisterDTOReponse;
import com.demo.autocareer.model.User;

@Mapper(componentModel = "spring", uses={DistrictMapper.class, RoleMapper.class})
public interface RegisterMapper extends EntityMapper<RegisterDTORequest, User, RegisterDTOReponse> {

    @Override
    RegisterDTOReponse mapEntityToResponse(User user);

    @Override
    User mapRequestToEntity(RegisterDTORequest dto);
}
