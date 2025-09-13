package com.demo.autocareer.dto.response;

import java.util.Date;

import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.enums.BehaviorType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentBehaviorDTOResponse {
    private Job job;
    private BehaviorType behaviorType;
    private Date timestamp;
}
