package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.SemesterDTO;
import com.demo.autocareer.model.Semester;

@Mapper(componentModel = "spring")
public interface SemesterMapper {
    SemesterDTO toDto(Semester dto);
}
