package com.example.demo.mapper;

import com.example.demo.dto.auth.SignupRequest;
import com.example.demo.dto.auth.UserProfileResponse;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper
{
    @Mapping(target = "passwordHash",source = "password")
   User toEntity(SignupRequest signupRequest);

    //@Mapping(target = "",source = "")
    UserProfileResponse toProfile(User user);
}
