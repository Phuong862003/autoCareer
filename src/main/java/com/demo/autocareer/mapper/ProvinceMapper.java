package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.ProvinceDTO;
import com.demo.autocareer.model.JobProvince;
import com.demo.autocareer.model.Province;

@Mapper(componentModel = "spring")
public interface ProvinceMapper {
    ProvinceDTO toDto(Province province);

    default ProvinceDTO toDto(JobProvince jobProvince) {
        if (jobProvince == null || jobProvince.getProvince() == null) {
            return null;
        }
        return toDto(jobProvince.getProvince());
    }
}
