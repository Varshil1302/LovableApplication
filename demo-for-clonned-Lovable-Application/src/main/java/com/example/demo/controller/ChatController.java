package com.example.demo.controller;

import com.example.demo.service.AiCodeGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController
{
    private final AiCodeGenerationService aiCodeGenerationService;


}
