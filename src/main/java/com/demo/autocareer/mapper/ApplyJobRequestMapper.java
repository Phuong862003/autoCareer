package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.request.ApplyDTORequest;
import com.demo.autocareer.dto.response.ApplyRequestDTOReponse;
import com.demo.autocareer.model.ApplyJob;

@Mapper(componentModel = "spring")
public interface  ApplyJobRequestMapper extends EntityMapper<ApplyDTORequest, ApplyJob, ApplyRequestDTOReponse>{
    @Override
    ApplyRequestDTOReponse mapEntityToResponse(ApplyJob applyJob);
}
