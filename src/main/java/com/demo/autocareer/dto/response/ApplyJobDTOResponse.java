package com.demo.autocareer.dto.response;

import java.io.Serializable;
import java.util.Date;

import com.demo.autocareer.model.enums.ApplyJobStatus;
import com.demo.autocareer.model.enums.JobStatus;

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
public class ApplyJobDTOResponse implements Serializable {
    private Long id;
    private JobDTOResponse job;
    private String cover_letter;
    private ApplyJobStatus applyJobStatus;
    private String attachment;
    private StudentDTOResponse student;
    private Date createdAt;
    private Date updatedAt;
}
