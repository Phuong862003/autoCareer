package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.request.InternSemesterDTORequest;
import com.demo.autocareer.dto.response.InternSemesterDTOResponse;
import com.demo.autocareer.model.InternshipSemester;

@Mapper(componentModel = "spring", uses={StudentMapper.class, SemesterMapper.class})
public interface InternSemesterMapper extends EntityMapper<InternSemesterDTORequest, InternshipSemester, InternSemesterDTOResponse>{
    @Override
    InternSemesterDTOResponse mapEntityToResponse(InternshipSemester dto);
    
}
