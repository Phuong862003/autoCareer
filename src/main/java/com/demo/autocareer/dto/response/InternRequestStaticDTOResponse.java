package com.demo.autocareer.dto.response;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternRequestStaticDTOResponse {
    private long totalInternRequest;
    private long approvedRequest;
    private long completedRequest;
    private long totalUni;
    private Map<Integer, Long> monthlyRequestStats;
}