package com.demo.autocareer.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SubFieldDTO implements Serializable{
    private Long id;
    private String sub_field_name;
}
