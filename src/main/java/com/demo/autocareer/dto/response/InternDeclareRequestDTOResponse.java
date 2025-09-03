package com.demo.autocareer.dto.response;

import java.io.Serializable;
import java.util.Date;

import com.demo.autocareer.dto.StudentDTO;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.Semester;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Data
public class InternDeclareRequestDTOResponse implements Serializable{
    private Long id;
    private Semester semester;
    private String companyName;
    private String companyAddress;
    private String position;
    private String contactPerson;
    private String contactPhone;
    private String websiteUrl;
    private String statusIntern;
    private JobDTOResponse job;
    private StudentDTOResponse student;
    private Date createdAt;
    private Date updatedAt;
}
