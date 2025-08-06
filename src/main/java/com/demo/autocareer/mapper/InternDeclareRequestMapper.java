package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.request.ApplyJobDTORequest;
import com.demo.autocareer.dto.request.InternDeclareRequestDTORequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.InternDeclareRequestDTOResponse;
import com.demo.autocareer.model.ApplyJob;
import com.demo.autocareer.model.InternDeclareRequest;

@Mapper(componentModel = "spring", uses={JobMapper.class, StudentMapper.class})
public interface  InternDeclareRequestMapper extends EntityMapper<InternDeclareRequestDTORequest, InternDeclareRequest, InternDeclareRequestDTOResponse>{
    @Override
    InternDeclareRequestDTOResponse mapEntityToResponse(InternDeclareRequest internDeclareRequest);
}
