package com.example.demo.dto.auth;

public record SignupRequest(
        String email,
        String name,
        String password
) {
}
