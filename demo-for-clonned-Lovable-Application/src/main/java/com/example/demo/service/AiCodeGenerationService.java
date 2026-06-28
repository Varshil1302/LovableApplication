package com.example.demo.service;

import com.example.demo.dto.chat.StreamResponse;
import reactor.core.publisher.Flux;

public interface AiCodeGenerationService
{
    Flux<StreamResponse> streamResponse(String message, Long projectId);
}
