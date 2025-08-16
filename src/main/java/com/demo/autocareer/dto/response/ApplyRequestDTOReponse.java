package com.demo.autocareer.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.demo.autocareer.model.enums.ApplyJobStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplyRequestDTOReponse implements Serializable{
    private Long id;
    private ApplyJobStatus applyJobStatus;
    private String approvedBy;
    private LocalDateTime approvedAt;
}
