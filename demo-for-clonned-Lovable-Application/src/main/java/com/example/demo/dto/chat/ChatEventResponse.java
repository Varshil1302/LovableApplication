package com.example.demo.dto.chat;

import com.example.demo.enums.ChatEventType;

public record ChatEventResponse(
        Long id,
        ChatEventType chatEventType,
        Integer sequenceOrder,
        String content,
        String filePath,
        String metadata
) {
}
