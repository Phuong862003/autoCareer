package com.demo.autocareer.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.demo.autocareer.model.enums.StatusIntern;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternRequestApprovedDTOReponse implements Serializable {
    private Long id;
    private StatusIntern statusIntern;
    private String note;
    private String approvedBy;
    private LocalDateTime approvedAt;
}
