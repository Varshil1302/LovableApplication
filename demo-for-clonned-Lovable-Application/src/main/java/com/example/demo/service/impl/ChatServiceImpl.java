package com.example.demo.service.impl;

import com.example.demo.dto.chat.ChatResponse;
import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.ChatSession;
import com.example.demo.entity.ChatSessionId;
import com.example.demo.mapper.ChatMessageMapper;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.ChatSessionRepository;
import com.example.demo.security.JwtService;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService
{

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final JwtService jwtService;
    private final ChatMessageMapper chatMessageMapper;

    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        Long userId = jwtService.getCurrentUser();
        ChatSession chatSession = chatSessionRepository.getReferenceById(ChatSessionId.builder().projectId(projectId).userId(userId).build());
        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatSession(chatSession);
        return chatMessageMapper.toChatMessage(chatMessageList);
    }
}
