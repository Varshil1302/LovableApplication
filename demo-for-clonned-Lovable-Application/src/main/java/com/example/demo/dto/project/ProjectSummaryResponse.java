package com.example.demo.dto.project;

import com.example.demo.enums.ProjectRole;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ProjectSummaryResponse(
        Long id,
        String name,
        ProjectRole role,
        Instant createdAt,
        Instant updatedAt
) {
}
