package com.demo.autocareer.dto.request;

import java.io.Serializable;

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
public class InternDeclareRequestDTORequest implements Serializable{
    private String semester;
    private String companyName;
    private String companyAddress;
    private String position;
    private String contactPerson;
    private String contactPhone;
    private String statusIntern; 
    private Long jobDetailId;  
    private Long studentId;

    public String getSemester() {
        return semester;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public String getPosition() {
        return position;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getStatusIntern() {
        return statusIntern;
    }

    public Long getJobDetailId() {
        return jobDetailId;
    }

    public Long getStudentId() {
        return studentId;
    }
}
