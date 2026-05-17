package com.example.demo.service;

import reactor.core.publisher.Flux;

public interface AiCodeGenerationService
{
    Flux<String> streamResponse(String message,Long projectId);
}
