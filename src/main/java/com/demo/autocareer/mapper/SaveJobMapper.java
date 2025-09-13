package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.request.SaveJobDTORequest;
import com.demo.autocareer.dto.response.SaveJobDTOResponse;
import com.demo.autocareer.model.SaveJob;

@Mapper(componentModel = "spring", uses = {StudentMapper.class, JobMapper.class})
public interface SaveJobMapper extends EntityMapper<SaveJobDTORequest, SaveJob, SaveJobDTOResponse> {
    @Override
    SaveJobDTOResponse mapEntityToResponse(SaveJob saveJob);

    @Override
    SaveJob mapRequestToEntity(SaveJobDTORequest dto);
}

