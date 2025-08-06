package com.demo.autocareer.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FacultyDTO implements Serializable{
    private Long id;
    private String faculty_name;

    public String getFaculty_name(){
        return faculty_name;
    }
}
