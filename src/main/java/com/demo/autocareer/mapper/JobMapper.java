package com.demo.autocareer.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.demo.autocareer.dto.ProvinceDTO;
import com.demo.autocareer.dto.request.JobDTORequest;
import com.demo.autocareer.dto.response.JobDTOResponse;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.JobProvince;



@Mapper(componentModel = "spring", uses = { OrganizationMapper.class, FieldMapper.class, ProvinceMapper.class })
public interface JobMapper extends EntityMapper<JobDTORequest, Job, JobDTOResponse> {

    @Override
    Job mapRequestToEntity(JobDTORequest dto);

    @Override
    @Mapping(target = "provinces", expression = "java(mapProvinces(job.getJobProvinces()))")
    JobDTOResponse mapEntityToResponse(Job job);

    @Override
    void partialUpdate(@MappingTarget Job entity, JobDTORequest dto);

    default List<ProvinceDTO> mapProvinces(List<JobProvince> jobProvinces) {
        if (jobProvinces == null) {
            return null;
        }
        return jobProvinces.stream().map(jp -> {
            ProvinceDTO dto = new ProvinceDTO();
            dto.setId(jp.getProvince().getId());
            dto.setProvinceName(jp.getProvince().getProvinceName());
            return dto;
        }).collect(Collectors.toList());
    }
}
