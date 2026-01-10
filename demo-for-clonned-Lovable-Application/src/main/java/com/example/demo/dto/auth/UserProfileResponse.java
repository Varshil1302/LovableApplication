package com.example.demo.dto.auth;

public record UserProfileResponse(
        Long userId,
        String email,
        String name,
        String avatarUrl
) {
}
