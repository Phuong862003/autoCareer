package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.demo.autocareer.dto.request.JobDTORequest;
import com.demo.autocareer.dto.response.JobDTOResponse;
import com.demo.autocareer.model.Job;



@Mapper(componentModel = "spring", uses={OrganizationMapper.class})
public interface  JobMapper extends EntityMapper<JobDTORequest, Job, JobDTOResponse> {

    @Override
    Job mapRequestToEntity(JobDTORequest dto);

    @Override
    JobDTOResponse mapEntityToResponse(Job job);

    @Override
    void partialUpdate(@MappingTarget Job entity, JobDTORequest dto);
}
