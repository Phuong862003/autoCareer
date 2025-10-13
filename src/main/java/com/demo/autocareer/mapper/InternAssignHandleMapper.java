package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.request.InternAssignHandleRequest;
import com.demo.autocareer.dto.response.InternAssignHandleResponse;
import com.demo.autocareer.model.InternshipRequestFile;

@Mapper(componentModel = "spring", uses={OrganizationMapper.class})
public interface InternAssignHandleMapper extends EntityMapper<InternAssignHandleRequest, InternshipRequestFile, InternAssignHandleResponse>{
    @Override
    InternAssignHandleResponse mapEntityToResponse(InternshipRequestFile dto);
}