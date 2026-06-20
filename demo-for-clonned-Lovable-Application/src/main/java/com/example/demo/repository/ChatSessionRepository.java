package com.example.demo.repository;

import com.example.demo.entity.ChatSession;
import com.example.demo.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId>
{

}
