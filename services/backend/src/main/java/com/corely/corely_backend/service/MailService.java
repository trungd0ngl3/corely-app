package com.corely.corely_backend.service;

import com.corely.corely_backend.entity.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailService {

    JavaMailSender mailSender;

    @NonFinal
    @Value("${app.frontend-url}")
    String frontendUrl;

    public void sendVerificationMail(User user, String token) {
        String link = frontendUrl + "/verify-email?token=" + token;

        send(
                user.getEmail(),
                "Verify your Corely account",
                """
                        Welcome to Corely.

                        Click the link below to verify your account.

                        %s

                        This link expires in 15 minutes.
                        """.formatted(link));
    }

    public void sendPasswordResetEmail(User user, String token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        send(
                user.getEmail(),
                "Reset your password",
                """
                        Hello %s,

                        We received a request to reset your password.

                        Click the link below:

                        %s

                        This link expires in 15 minutes.

                        If you didn't request this, please ignore this email.

                        Corely Team
                        """.formatted(user.getFullName(), resetUrl));
    }

    public void send(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
}