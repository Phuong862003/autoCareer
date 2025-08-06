package com.demo.autocareer.filter;

import com.demo.autocareer.dto.request.BaseFilterRequest;
public class InternshipRequestFilter extends BaseFilterRequest {
    private Long universityId;

    public Long getUniversityId() {
        return universityId;
    }

    public void setUniversityId(Long universityId) {
        this.universityId = universityId;
    }
    
}

