package com.example.demo.dto.chat;

import com.example.demo.entity.ChatEvent;
import com.example.demo.entity.ChatSession;
import com.example.demo.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponse(
        Long id,
        ChatSession chatSession,
        String content,
        MessageRole role,
        List<ChatEventResponse> chatEventList,
        Integer tokenUsed,
        Instant createdAt
) {
}
