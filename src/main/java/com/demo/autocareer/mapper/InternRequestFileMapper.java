package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.response.InternRequestFileDTOResponse;
import com.demo.autocareer.model.InternshipRequestFile;

@Mapper(componentModel = "spring", uses={InternshipMapper.class})
public interface  InternRequestFileMapper {
    InternRequestFileDTOResponse toDto(InternshipRequestFile dto);
}
