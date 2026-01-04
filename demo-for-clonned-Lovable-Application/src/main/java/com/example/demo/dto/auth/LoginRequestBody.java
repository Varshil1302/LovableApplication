package com.example.demo.dto.auth;

public record LoginRequestBody(
        String email,
        String password
) {
}
