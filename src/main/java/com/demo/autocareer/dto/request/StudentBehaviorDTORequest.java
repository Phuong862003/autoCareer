package com.demo.autocareer.dto.request;

import java.util.Date;
import java.util.List;

import com.demo.autocareer.model.enums.BehaviorType;

import lombok.Data;

@Data
public class StudentBehaviorDTORequest {
    private List<Long> jobIds;
    private BehaviorType behaviorType;
    private Date timestamp ;

    public StudentBehaviorDTORequest(List<Long> jobIds, BehaviorType behaviorType, Date timestamp) {
        this.jobIds = jobIds;
        this.behaviorType = behaviorType;
        this.timestamp = timestamp;
    }

    public List<Long> getJobId(){
        return jobIds;
    }

    public BehaviorType getBehaviorType(){
        return behaviorType;
    }
}
