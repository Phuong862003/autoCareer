package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.FieldDTO;
import com.demo.autocareer.model.Field;

@Mapper(componentModel = "spring")
public interface FieldMapper {
    FieldDTO toDto(Field field);
}