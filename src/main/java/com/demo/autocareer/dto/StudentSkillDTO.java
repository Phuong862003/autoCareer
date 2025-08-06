package com.demo.autocareer.dto;

import java.io.Serializable;

import com.demo.autocareer.dto.response.StudentDTOResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StudentSkillDTO implements Serializable{
    private Long id;
    // private StudentDTOResponse student;
    private SkillDTO skill;
    public SkillDTO getSkill(){
        return skill;
    }
}
