package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name="application_user")
@AllArgsConstructor
@NoArgsConstructor
public class User
{
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      Long userId;

      @Column(name = "userEmail",nullable = false)
      String email;

      String passwordHash;

      @Column(nullable = false)
      String name;

      String avatarUrl;

      @CreationTimestamp
      Instant createdAt;

      @UpdateTimestamp
      Instant updatedAt;

      Instant deletedAt;
}
