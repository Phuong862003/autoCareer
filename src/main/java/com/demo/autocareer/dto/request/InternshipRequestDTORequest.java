package com.demo.autocareer.dto.request;

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
public class InternshipRequestDTORequest {
    private String title;
    private String description;
    private String requirement;
    private Integer quality;
    private String requestMessage;
    private Long companyId;

    public Long getCompanyId(){
        return companyId;
    }

    public String getTitle(){
        return title;
    }
}
