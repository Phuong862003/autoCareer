package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.request.ApplyJobDTORequest;
import com.demo.autocareer.dto.request.RegisterDTORequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.RegisterDTOReponse;
import com.demo.autocareer.dto.response.StudentDTOResponse;
import com.demo.autocareer.model.ApplyJob;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.model.User;
import com.demo.autocareer.service.ApplyJobService;

@Mapper(componentModel = "spring", uses={JobMapper.class, StudentMapper.class})
public interface  ApplyJobMapper extends EntityMapper<ApplyJobDTORequest, ApplyJob, ApplyJobDTOResponse> {
    @Override
    ApplyJobDTOResponse mapEntityToResponse(ApplyJob applyJob);
}
