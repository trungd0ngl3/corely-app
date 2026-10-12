package com.corely.corely_backend.repository;

import com.corely.corely_backend.entity.PasswordResetToken;
import com.corely.corely_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);

    void deleteByUser(User user);

    void deleteByExpiresAtBefore(LocalDateTime time);
}