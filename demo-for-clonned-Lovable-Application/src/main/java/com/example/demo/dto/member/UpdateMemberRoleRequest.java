package com.example.demo.dto.member;


import com.example.demo.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(
        @NotNull  ProjectRole role) {
}
