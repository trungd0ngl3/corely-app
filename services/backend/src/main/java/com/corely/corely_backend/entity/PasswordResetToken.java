package com.corely.corely_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens", indexes = {
        @Index(name = "idx_password_reset_token", columnList = "token"),
        @Index(name = "idx_password_reset_user", columnList = "user_id")}
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false, unique = true, length = 255)
    String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    User user;

    @Column(nullable = false)
    LocalDateTime expiresAt;

    @Builder.Default
    @Column(nullable = false)
    boolean used = false;

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }
}