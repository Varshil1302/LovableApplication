package com.example.demo.dto.member;


import com.example.demo.enums.ProjectRole;

public record InviteMemberRequest(
        String email,
        ProjectRole role
) {
}
