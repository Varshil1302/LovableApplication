package com.example.demo.service;

import com.example.demo.dto.chat.ChatResponse;

import java.util.List;

public interface ChatService
{
    List<ChatResponse>  getProjectChatHistory(Long projectId);
}
