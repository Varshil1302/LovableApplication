package com.example.demo.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotNull @Email String email,
        String name,
        @Size(min = 4,max = 18,message = "Password Should be within 4 to 18.")  String password
) {
}
