package com.demo.autocareer.dto.response;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyStaticDTOResponse {
    private long totalJobs;
    private long activeJobs;
    private long expiredJobs;
    private long pendingJobs;
    private long totalApplicants;
    private long hiredApplicants;
    private Map<Integer, Long> monthlyJobStats;
}
