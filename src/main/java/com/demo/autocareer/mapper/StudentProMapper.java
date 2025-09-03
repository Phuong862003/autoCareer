package com.demo.autocareer.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.demo.autocareer.dto.StudentDTO;
import com.demo.autocareer.dto.request.StudentDTORequest;
import com.demo.autocareer.dto.response.StudentDTOResponse;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.model.enums.StatusIntern;

@Mapper(componentModel = "spring", uses = {DistrictMapper.class, OrganizationFacultyMapper.class, SubFieldMapper.class, StudentSkillMapper.class})
public interface StudentProMapper extends EntityMapper<StudentDTORequest, Student, StudentDTOResponse> {
    @Override
    @Mapping(target = "statusIntern", expression = "java(mapStatusIntern(student))")
    StudentDTOResponse mapEntityToResponse(Student student);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Student student, StudentDTORequest studentDTO);

    default StatusIntern mapStatusIntern(Student student) {
        if (student.getInternDeclareRequest() == null) {
            return StatusIntern.NOT_YET;
        }
        return student.getInternDeclareRequest().getStatusIntern();
    }
}
