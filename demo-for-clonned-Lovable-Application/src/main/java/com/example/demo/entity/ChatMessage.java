package com.example.demo.entity;

import com.example.demo.enums.MessageRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "chat_message")
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JoinColumns({
            @JoinColumn(name = "project_id",referencedColumnName = "id",nullable = false),
            @JoinColumn(name = "user_id",referencedColumnName = "userId",nullable = false)
    })
    ChatSession chatSession;

    @Column(columnDefinition = "text",nullable = false)
    String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    MessageRole role;


    Integer tokenUsed=0;

    @CreationTimestamp
    Instant createdAt;
}
