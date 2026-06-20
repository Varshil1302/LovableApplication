package com.example.demo.entity;

import com.example.demo.enums.MessageRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "chat_message")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "project_id",referencedColumnName = "project_id",nullable = false),
            @JoinColumn(name = "user_id",referencedColumnName = "user_id",nullable = false)
    })
    ChatSession chatSession;

    @Column(columnDefinition = "text",nullable = false)
    String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    MessageRole role;

    @OneToMany(mappedBy = "chatMessage",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @OrderBy("sequenceOrder ASC")
    List<ChatEvent> chatEventList;

    Integer tokenUsed=0;

    @CreationTimestamp
    Instant createdAt;
}
