package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.FacultyDTO;
import com.demo.autocareer.model.Faculty;

@Mapper(componentModel = "spring")
public interface FacultyMapper {
    FacultyDTO toDto(Faculty faculty);
}
