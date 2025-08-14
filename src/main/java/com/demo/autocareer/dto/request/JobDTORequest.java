package com.demo.autocareer.dto.request;

import java.io.Serializable;
import java.util.List;

import com.demo.autocareer.dto.FieldDTO;
import com.demo.autocareer.dto.OrganizationDTO;
import com.demo.autocareer.model.enums.Gender;
import com.demo.autocareer.model.enums.JobStatus;
import com.demo.autocareer.model.enums.WorkingType;

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
public class JobDTORequest implements Serializable{
    private String title;
    private String description;
    private String requirement;
    private String welfare;
    private int salary_start;
    private int salary_end;
    Gender gender;
    JobStatus jobStatus;
    private int quality;
    private String working_time;
    WorkingType workingType;
    private String working_address;
    List<Long> subfieldIds;

    public List<Long> getSubFieldIds(){
        return subfieldIds;
    }
}
