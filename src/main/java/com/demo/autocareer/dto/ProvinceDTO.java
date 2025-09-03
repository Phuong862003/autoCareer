package com.demo.autocareer.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProvinceDTO implements Serializable{
    private Long id;
    private String provinceName;

    public void setId(Long id) {
        this.id = id;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
