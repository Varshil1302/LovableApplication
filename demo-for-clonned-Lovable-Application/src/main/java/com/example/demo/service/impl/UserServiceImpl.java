package com.example.demo.service.impl;

import com.example.demo.dto.auth.UserProfileResponse;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService
{

    @Override
    public UserProfileResponse getProfile(Long userId) {
        return null;
    }
}
