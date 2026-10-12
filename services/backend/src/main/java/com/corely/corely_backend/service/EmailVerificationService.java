package com.corely.corely_backend.service;

import com.corely.corely_backend.entity.EmailVerificationToken;
import com.corely.corely_backend.entity.User;
import com.corely.corely_backend.exception.AppException;
import com.corely.corely_backend.exception.ErrorCode;
import com.corely.corely_backend.repository.EmailVerificationTokenRepository;
import com.corely.corely_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class EmailVerificationService {

    EmailVerificationTokenRepository emailVerificationTokenRepository;
    UserRepository userRepository;
    MailService mailService;

    @Transactional
    public void createTokenAndSend(User user) {
        emailVerificationTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        emailVerificationTokenRepository.save(EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiryTime(LocalDateTime.now().plusMinutes(15))
                .build());

        mailService.sendVerificationMail(user, token);
    }

    @Transactional
    public void verify(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_VERIFICATION_TOKEN));

        if (verificationToken.isUsed()) {
            throw new AppException(ErrorCode.VERIFICATION_TOKEN_ALREADY_USED);
        }

        if (verificationToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.VERIFICATION_TOKEN_EXPIRED);
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        user.setIsActive(true);
        userRepository.save(user);

        verificationToken.setUsed(true);
        emailVerificationTokenRepository.save(verificationToken);
    }

    @Transactional
    public void resend(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        createTokenAndSend(user);
    }
}