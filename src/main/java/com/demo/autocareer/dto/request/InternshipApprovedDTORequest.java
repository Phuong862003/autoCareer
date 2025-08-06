package com.demo.autocareer.dto.request;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.demo.autocareer.model.enums.StatusRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipApprovedDTORequest{
    private StatusRequest statusRequest;
    private String note;

    public StatusRequest getStatusRequest() {
        return statusRequest;
    }

    public String getNote() {
        return note;
    }
}
