package com.example.demo.service.impl;

import com.example.demo.service.AiCodeGenerationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AiCodeGenerationServiceImpl implements AiCodeGenerationService
{

    @Override
    public Flux<String> streamResponse(String message, Long projectId) {
        return null;
    }
}
