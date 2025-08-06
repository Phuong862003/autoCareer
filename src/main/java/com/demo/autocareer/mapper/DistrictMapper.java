package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.DistrictDTO;
import com.demo.autocareer.model.District;

@Mapper(componentModel = "spring", uses={ProvinceMapper.class})
public interface DistrictMapper {
    DistrictDTO toDTO(District district);
    District toEntity(DistrictDTO dto);
}
