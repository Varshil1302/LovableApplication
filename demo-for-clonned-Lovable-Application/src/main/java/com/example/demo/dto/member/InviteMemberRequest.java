package com.example.demo.dto.member;

import com.codingshuttle.projects.lovable_clone.enums.ProjectRole;

public record InviteMemberRequest(
        String email,
        ProjectRole role
) {
}
