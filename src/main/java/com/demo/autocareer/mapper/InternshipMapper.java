package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.demo.autocareer.dto.request.InternshipRequestDTORequest;
import com.demo.autocareer.dto.response.InternshipRequestDTOResponse;
import com.demo.autocareer.model.InternshipRequest;

@Mapper(componentModel = "spring", uses={OrganizationMapper.class})
public interface InternshipMapper extends EntityMapper<InternshipRequestDTORequest, InternshipRequest, InternshipRequestDTOResponse>{
    @Override
    @Mapping(source = "companyId", target = "company")
    InternshipRequest mapRequestToEntity(InternshipRequestDTORequest request);

    @Override
    InternshipRequestDTOResponse mapEntityToResponse(InternshipRequest dto);
}
   