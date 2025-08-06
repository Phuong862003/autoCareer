package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.ProvinceDTO;
import com.demo.autocareer.model.Province;

@Mapper(componentModel = "spring")
public interface ProvinceMapper {
    ProvinceDTO toDto(Province province);
}
