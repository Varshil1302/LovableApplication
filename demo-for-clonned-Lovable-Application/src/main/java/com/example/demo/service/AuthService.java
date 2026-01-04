package com.example.demo.service;

import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.LoginRequestBody;
import com.example.demo.dto.auth.SignupRequest;
import com.example.demo.dto.auth.UserProfileResponse;
import org.jspecify.annotations.Nullable;

public interface AuthService
{

     AuthResponse login(LoginRequestBody loginRequest);

     UserProfileResponse signup(SignupRequest signupRequest);
}
