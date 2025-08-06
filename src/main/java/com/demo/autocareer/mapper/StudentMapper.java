package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.demo.autocareer.dto.StudentDTO;
import com.demo.autocareer.dto.response.StudentDTOResponse;
import com.demo.autocareer.model.Student;

@Mapper(componentModel = "spring", uses = {DistrictMapper.class, OrganizationFacultyMapper.class, SubFieldMapper.class, StudentSkillMapper.class})
public interface StudentMapper extends EntityMapper<StudentDTO, Student, StudentDTOResponse> {
    @Override
    StudentDTOResponse mapEntityToResponse(Student student);

    @Override
    void partialUpdate(@MappingTarget Student student, StudentDTO studentDTO);
}

