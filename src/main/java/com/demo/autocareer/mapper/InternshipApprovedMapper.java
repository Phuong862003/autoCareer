package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.demo.autocareer.dto.request.InternshipApprovedDTORequest;
import com.demo.autocareer.dto.response.InternshipApprovedDTOResponse;
import com.demo.autocareer.model.InternshipRequest;

@Mapper(componentModel = "spring", uses={OrganizationMapper.class})
public interface InternshipApprovedMapper extends EntityMapper<InternshipApprovedDTORequest, InternshipRequest, InternshipApprovedDTOResponse>{
    @Override
    InternshipApprovedDTOResponse mapEntityToResponse(InternshipRequest dto);
}
