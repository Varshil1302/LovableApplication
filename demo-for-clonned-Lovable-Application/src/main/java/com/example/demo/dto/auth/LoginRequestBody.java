package com.example.demo.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequestBody(
     @NotNull @Email String email,
     @Size(min = 4,max = 8,message = "Password Should be within 4 to 8.")  String password
) {
}
