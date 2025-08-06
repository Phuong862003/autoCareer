package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.request.InternRequestApprovedDTORequest;
import com.demo.autocareer.dto.response.InternRequestApprovedDTOReponse;
import com.demo.autocareer.model.InternDeclareRequest;

@Mapper(componentModel = "spring", uses={JobMapper.class, StudentMapper.class})
public interface  InternDeclareApprovedMapper extends EntityMapper<InternRequestApprovedDTORequest, InternDeclareRequest, InternRequestApprovedDTOReponse>{
    InternRequestApprovedDTOReponse toDto(InternDeclareRequest internDeclareRequest);

    @Override
    InternRequestApprovedDTOReponse mapEntityToResponse(InternDeclareRequest dto);
}
