package com.demo.autocareer.dto.response;

import java.io.Serializable;
import java.util.Date;

import com.demo.autocareer.model.enums.Gender;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternshipAssignDTOResponse implements Serializable{
    private InternshipRequestDTOResponse internshipRequest;
    private String studentCode;
    private String name;
    private String email;
    private String phoneNumber;
    private Integer graduatedYear;
    private Gender gender;
    private Date dob;
    private String facultyName;
    private String skill;
}
