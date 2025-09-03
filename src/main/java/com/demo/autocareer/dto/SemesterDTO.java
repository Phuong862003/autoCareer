package com.demo.autocareer.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class SemesterDTO implements Serializable{
    private Long id;
    private String code;
    private String name;
}
