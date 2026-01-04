package com.example.demo.service;

import com.example.demo.dto.auth.UserProfileResponse;
import org.jspecify.annotations.Nullable;

public interface UserService
{
    UserProfileResponse getProfile(Long userId);
}
