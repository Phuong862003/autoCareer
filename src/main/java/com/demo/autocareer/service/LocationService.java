package com.demo.autocareer.service;

import java.util.List;

import com.demo.autocareer.dto.DistrictDTO;
import com.demo.autocareer.dto.ProvinceDTO;
import com.demo.autocareer.dto.SemesterDTO;
import com.demo.autocareer.dto.OrganizationDTO;

public interface LocationService {
    List<ProvinceDTO> findAll();
    List<DistrictDTO> findByProvinceId(Long id);
    List<SemesterDTO> findAllSemester();
    OrganizationDTO getOrganizationDetail(Long id);
}
