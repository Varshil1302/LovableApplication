package com.example.demo.service.impl;

import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.LoginRequestBody;
import com.example.demo.dto.auth.SignupRequest;
import com.example.demo.dto.auth.UserProfileResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.BadRequestException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class AuthServiceImp implements AuthService
{

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;

    @Override
    public AuthResponse login(LoginRequestBody loginRequest)
    {
        boolean isExists=userRepository.existsByEmail(loginRequest.email());
        if(!isExists) throw new BadRequestException("User is not exsits now...");
        User user=userRepository.findByEmail(loginRequest.email()).orElse(null);
        boolean isPassMatch= BCrypt.checkpw(loginRequest.password(),user.getPasswordHash());
        if(!isPassMatch) throw  new BadRequestException("Password mismatch");
        String jwtToken=jwtService.generateJWT(user);
        return new AuthResponse(jwtToken,userMapper.toProfile(user));
    }

    @Override
    public UserProfileResponse signup(SignupRequest signupRequest)
    {
        userRepository.findByEmail(signupRequest.email()).ifPresent(user->{
           throw new BadRequestException("User is already exists");
        });
        User user=userMapper.toEntity(signupRequest);
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        userRepository.save(user);
        return new UserProfileResponse(user.getUserId(), user.getEmail(), user.getName(), user.getAvatarUrl());
    }
}
