package com.example.demo.controller;

import com.example.demo.dto.chat.ChatRequest;
import com.example.demo.service.AiCodeGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class ChatController
{
    private final AiCodeGenerationService aiCodeGenerationService;

    @PostMapping(path = "/api/chat/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@RequestBody ChatRequest chatRequest){
        return aiCodeGenerationService
                .streamResponse(chatRequest.message(),chatRequest.projectId())
                .map(data-> ServerSentEvent.<String>builder()
                        .data(data)
                        .build());
    }


}
