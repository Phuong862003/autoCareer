package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.request.StudentBehaviorDTORequest;
import com.demo.autocareer.dto.response.StudentBehaviorDTOResponse;
import com.demo.autocareer.model.StudentBehavior;


@Mapper(componentModel = "spring")
public interface StudentBehaviorMapper extends EntityMapper<StudentBehaviorDTORequest, StudentBehavior, StudentBehaviorDTOResponse>{
    StudentBehaviorDTOResponse toDTO(StudentBehavior dto);
}
