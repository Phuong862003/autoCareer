package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.SkillDTO;
import com.demo.autocareer.model.Skill;

@Mapper(componentModel = "spring")
public interface  SkillMapper {
    SkillDTO toDo(Skill skill);
}
