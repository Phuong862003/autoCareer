package com.demo.autocareer.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FieldDTO implements Serializable{
    private Long id;
    private String field_name;
    private List<SubFieldDTO> subfields;
}
