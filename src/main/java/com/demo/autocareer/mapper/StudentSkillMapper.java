package com.demo.autocareer.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.StudentSkillDTO;
import com.demo.autocareer.model.StudentSkill;

@Mapper(componentModel = "spring", uses = {SkillMapper.class})
public interface StudentSkillMapper extends EntityMapper<Object, StudentSkill, StudentSkillDTO> {
    @Override
    StudentSkillDTO mapEntityToResponse(StudentSkill entity);
    List<StudentSkillDTO> toDtoList(List<StudentSkill> studentSkills);
}


