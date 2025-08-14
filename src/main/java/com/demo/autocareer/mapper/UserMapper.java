package com.demo.autocareer.mapper;

import org.mapstruct.Mapper;

import com.demo.autocareer.dto.response.UserDTOResponse;
import com.demo.autocareer.model.User;

@Mapper(componentModel = "spring")
public interface  UserMapper{
    UserDTOResponse toDto(User dto);
}
