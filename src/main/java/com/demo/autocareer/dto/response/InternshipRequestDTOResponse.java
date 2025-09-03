package com.demo.autocareer.dto.response;

import com.demo.autocareer.dto.OrganizationDTO;
import com.demo.autocareer.model.enums.StatusRequest;

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
public class InternshipRequestDTOResponse {
    private Long id;
    private String title;
    private String description;
    private String requirement;
    private Integer quality;
    private String requestMessage;
    private StatusRequest statusRequest;
    private String note;
    private OrganizationDTO company;
    private OrganizationDTO university; 
}
