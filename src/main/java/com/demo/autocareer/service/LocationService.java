package com.demo.autocareer.service;

import java.util.List;

import com.demo.autocareer.dto.DistrictDTO;
import com.demo.autocareer.dto.ProvinceDTO;

public interface LocationService {
    List<ProvinceDTO> findAll();
    List<DistrictDTO> findByProvinceId(Long id);
}
