package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name="application_user")
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }
}
