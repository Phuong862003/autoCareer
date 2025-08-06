package com.demo.autocareer.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SkillDTO implements Serializable{
    private Long id;
    private String skillName;

    public String getSkillName(){
        return skillName;
    }
}
