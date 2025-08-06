package com.demo.autocareer.dto;

import java.io.Serializable;

import com.demo.autocareer.model.Faculty;
import com.demo.autocareer.model.Organization;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrganizationFacultyDTO implements Serializable{
    private Long id;
    OrganizationDTO organization;
    FacultyDTO faculty;

    public FacultyDTO getFaculty() {
        return faculty;
    }
}
