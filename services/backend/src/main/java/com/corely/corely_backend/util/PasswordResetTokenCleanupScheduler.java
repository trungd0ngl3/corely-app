package com.corely.corely_backend.util;

import com.corely.corely_backend.repository.PasswordResetTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
@Slf4j
public class PasswordResetTokenCleanupScheduler {
    PasswordResetTokenRepository repository;
    @Scheduled(cron = "0 0 * * * *") // mỗi giờ
    @Transactional
    public void cleanup() {
        log.info("Cleaning up Password Reset Tokens");
        repository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}