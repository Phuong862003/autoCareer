package com.demo.autocareer.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.autocareer.mapper.DistrictMapper;
import com.demo.autocareer.mapper.ProvinceMapper;
import com.demo.autocareer.repository.DistrictRepository;
import com.demo.autocareer.repository.ProvinceRepository;
import com.demo.autocareer.service.LocationService;
import com.demo.autocareer.dto.DistrictDTO;
import com.demo.autocareer.dto.ProvinceDTO;

@Service
public class LocationServiceImpl implements LocationService{
    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private DistrictMapper districtMapper;

    @Autowired
    private ProvinceMapper proviceMapper;

    @Override
    public List<ProvinceDTO> findAll(){
        return provinceRepository.findAll()
                .stream()
                .map(proviceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DistrictDTO> findByProvinceId(Long id){
        return districtRepository.findByProvinceId(id)
                .stream()
                .map(districtMapper::toDTO)
                .collect(Collectors.toList());
    }
}
