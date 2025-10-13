package com.demo.autocareer.dto.response;

import java.io.Serializable;

import com.demo.autocareer.model.enums.StatusRequestFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternAssignHandleResponse implements Serializable {
    private Long id;
    private StatusRequestFile status;
    private String note;
    private String approvedBy;
}