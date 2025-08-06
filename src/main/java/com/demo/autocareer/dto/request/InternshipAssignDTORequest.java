package com.demo.autocareer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipAssignDTORequest {
    private Long internshipRequestId;
    
    public Long getInternshipRequestId(){
        return internshipRequestId;
    }
}
