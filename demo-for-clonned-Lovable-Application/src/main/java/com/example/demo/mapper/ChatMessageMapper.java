package com.example.demo.mapper;

import com.example.demo.dto.chat.ChatResponse;
import com.example.demo.entity.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper
{
    List<ChatResponse> toChatMessage(List<ChatMessage> messages);
}
