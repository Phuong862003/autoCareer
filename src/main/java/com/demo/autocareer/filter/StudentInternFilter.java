package com.demo.autocareer.filter;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.model.enums.StatusIntern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentInternFilter extends BaseFilterRequest {
    private Long facultyId;
    private String semester;
    private String name;
    private String studentCode;
    private StatusIntern  statusIntern;
    private Long districtId;

    public Long getFacultyId(){
        return facultyId;
    }

    public String getSemester(){
        return semester;
    }

    public String getName(){
        return name;
    }

    public String getStudentCode(){
        return studentCode;
    }

    public StatusIntern getStatusIntern(){
        return statusIntern;
    }

    public Long getDistrictId(){
        return districtId;
    }
}
