package com.demo.autocareer.dto.response;

import java.io.Serializable;

import com.demo.autocareer.dto.SemesterDTO;
import com.demo.autocareer.dto.StudentDTO;
import com.demo.autocareer.model.enums.StatusInternSemester;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternSemesterDTOResponse implements Serializable{
    private Long id;
    private StudentDTOResponse student;
    private SemesterDTO semester;
    private StatusInternSemester status;

    public Long getId(){
        return id;
    }

    public StudentDTOResponse getStudent(){
        return student;
    }

    public SemesterDTO getSemester(){
        return semester;
    }

    public StatusInternSemester getStatus(){
        return status;
    }
}
