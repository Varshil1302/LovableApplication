package com.example.demo.controller;

import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.LoginRequestBody;
import com.example.demo.dto.auth.SignupRequest;
import com.example.demo.dto.auth.UserProfileResponse;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController
{
     private final AuthService authService;
     private final UserService userService;

     @PostMapping("/login")
     public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequestBody loginRequest)
     {
         return ResponseEntity.ok(authService.login(loginRequest));
     }

     @PostMapping("/signup")
     public ResponseEntity<UserProfileResponse> signup(@RequestBody SignupRequest signupRequest)
     {
         return ResponseEntity.ok(authService.signup(signupRequest));
     }

     @GetMapping("/me")
     public ResponseEntity<UserProfileResponse> getProfile()
     {
         Long userId=1L;
         return ResponseEntity.ok(userService.getProfile(userId));

     }

}
