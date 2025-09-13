package com.demo.autocareer.dto;

import com.demo.autocareer.dto.response.JobDTOResponse;
import com.demo.autocareer.model.Job;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendationDTOResponse {
    private Long id;
    private JobDTOResponse job;
    private Double score;

    public Long getId(){
        return id;
    }

    public Double getScore(){
        return score;
    }

    public void setJob(JobDTOResponse job) {
        this.job = job;
    }

    public void setScore(Double score){
        this.score = score;
    }

    public void setId(Long id){
        this.id = id;
    }
}
