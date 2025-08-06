package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.request.InternshipAssignDTORequest;
import com.demo.autocareer.dto.response.InternshipAssignDTOResponse;
import com.demo.autocareer.model.InternshipAssignment;
import com.demo.autocareer.model.InternshipRequest;

@Mapper(componentModel = "spring", uses={InternshipMapper.class})
public interface InternshipAssignMapper extends EntityMapper<InternshipAssignDTORequest, InternshipAssignment, InternshipAssignDTOResponse>{
    @Override
    InternshipAssignDTOResponse mapEntityToResponse(InternshipAssignment dto);
}
