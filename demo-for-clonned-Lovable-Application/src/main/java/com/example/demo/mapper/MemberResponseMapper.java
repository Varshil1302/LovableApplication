package com.example.demo.mapper;

import com.example.demo.dto.member.MemberResponse;
import com.example.demo.entity.ProjectMember;
import com.example.demo.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberResponseMapper
{
    @Mapping(source = "projectRole",target = "role")
    @Mapping(source = "user.userId",target = "userId")
    @Mapping(source = "user.name",target = "name")
    @Mapping(source = "user.email",target = "email")
    MemberResponse toMemberResponse(ProjectMember projectMember);

    @Mapping(source = "projectRole",target = "role")
    @Mapping(source = "user.userId",target = "userId")
    @Mapping(source = "user.name",target = "name")
    @Mapping(source = "user.email",target = "email")
    List<MemberResponse> toMemberResponse(List<ProjectMember> projectMember);


    @Mapping(target = "role", constant = "OWNER")
    //@JsonIgnore()
    MemberResponse fromUserOWNER(User user);
}
