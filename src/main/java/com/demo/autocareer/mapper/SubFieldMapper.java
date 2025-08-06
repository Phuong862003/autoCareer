package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.SubFieldDTO;
import com.demo.autocareer.model.SubField;

@Mapper(componentModel = "spring", uses = FieldMapper.class)
public interface SubFieldMapper {
    SubFieldDTO toDto(SubField subField);
}

