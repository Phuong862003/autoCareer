package com.demo.autocareer.dto.request;

import com.demo.autocareer.model.Student;

import lombok.Data;

@Data
public class SaveJobDTORequest {
    private Student student;
    private Long jobId;

    public Long getId(){
        return jobId;
    }

}
