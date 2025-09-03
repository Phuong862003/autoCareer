package com.demo.autocareer.dto;

import java.io.Serializable;

import com.demo.autocareer.model.Province;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DistrictDTO implements Serializable{
    private Long id;
    String districtName;
    ProvinceDTO province;
}
