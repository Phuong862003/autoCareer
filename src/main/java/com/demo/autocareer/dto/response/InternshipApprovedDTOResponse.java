package com.demo.autocareer.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.demo.autocareer.dto.OrganizationDTO;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.enums.StatusRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternshipApprovedDTOResponse implements Serializable{
    private Long id;
    private StatusRequest statusRequest;
    private String note;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private OrganizationDTO uni;
}
