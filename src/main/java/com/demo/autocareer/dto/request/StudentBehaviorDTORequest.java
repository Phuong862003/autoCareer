package com.demo.autocareer.dto.request;

import java.util.List;

import com.demo.autocareer.model.enums.BehaviorType;

import lombok.Data;

@Data
public class StudentBehaviorDTORequest {
    private List<Long> jobIds;
    private BehaviorType behaviorType;

    public List<Long> getJobId(){
        return jobIds;
    }

    public BehaviorType getBehaviorType(){
        return behaviorType;
    }
}
