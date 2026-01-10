package com.example.demo.service.impl;

import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.LoginRequestBody;
import com.example.demo.dto.auth.SignupRequest;
import com.example.demo.dto.auth.UserProfileResponse;
import com.example.demo.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImp implements AuthService
{

    @Override
    public AuthResponse login(LoginRequestBody loginRequest) {
        return null;
    }

    @Override
    public UserProfileResponse signup(SignupRequest signupRequest) {
        return null;
    }
}
