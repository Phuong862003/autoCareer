package com.demo.autocareer.filter;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.model.enums.StatusRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyFilter extends BaseFilterRequest  {
    private String organizationName;
    private StatusRequest status;

    public String getOrganizationName(){
        return organizationName;
    }
    
    public StatusRequest getStatusRequest(){
        return status;
    }
}
