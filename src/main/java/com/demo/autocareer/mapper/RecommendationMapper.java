package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.RecommendationDTOResponse;
import com.demo.autocareer.model.RecommendationJob;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    RecommendationDTOResponse toDTO(RecommendationJob recommendation);
}
