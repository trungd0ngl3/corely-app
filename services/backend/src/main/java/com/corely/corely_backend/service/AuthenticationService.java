package com.corely.corely_backend.service;

import com.corely.corely_backend.dto.request.auth.*;
import com.corely.corely_backend.dto.response.auth.AuthenticateResponse;
import com.corely.corely_backend.dto.response.auth.IntrospectResponse;
import com.corely.corely_backend.dto.response.auth.UserResponse;
import com.corely.corely_backend.entity.InvalidatedToken;
import com.corely.corely_backend.entity.PasswordResetToken;
import com.corely.corely_backend.entity.Role;
import com.corely.corely_backend.entity.User;
import com.corely.corely_backend.exception.AppException;
import com.corely.corely_backend.exception.ErrorCode;
import com.corely.corely_backend.repository.InvalidatedTokenRepository;
import com.corely.corely_backend.repository.PasswordResetTokenRepository;
import com.corely.corely_backend.repository.RoleRepository;
import com.corely.corely_backend.repository.UserRepository;
import com.corely.corely_backend.util.SecurityUtils;
import com.nimbusds.jose.*;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    JwtService jwtService;
    UserService userService;
    MailService mailService;

    UserRepository userRepository;
    RoleRepository roleRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    PasswordEncoder passwordEncoder;
    EmailVerificationService emailVerificationService;
    PasswordResetTokenRepository passwordResetTokenRepository;

    SecurityUtils securityUtils;

    public UserResponse register(UserCreationRequest request){
        UserResponse userResponse = userService.createUser(request);
        User user = userRepository.findById(userResponse.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        emailVerificationService.createTokenAndSend(user);
        return userResponse;
    }


    public IntrospectResponse introspect(IntrospectRequest request) {
        boolean isValid = true;
        try {
            jwtService.verifyToken(request.getToken(), false);
        } catch (AppException | JOSEException | ParseException e) {
            isValid = false;
        }
        return IntrospectResponse.builder().isValid(isValid).build();
    }

    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new AppException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVATED);
        }

        // Allow admin to login via local even if provider is not local
        if (!"local".equals(user.getProvider())
                && user.getRoles().stream().noneMatch(role -> "ADMIN".equals(role.getName()))) {
            throw new AppException(ErrorCode.USE_SOCIAL_LOGIN);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        log.info("User {} logged in", request.getEmail());
        return jwtService.generateTokenPair(user);
    }


    @Transactional
    public AuthenticateResponse refreshToken(String token) throws JOSEException, ParseException {
        SignedJWT signedJWT = jwtService.verifyToken(token, true);

        String jit = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryDate = signedJWT.getJWTClaimsSet().getExpirationTime();

        // Invalidate old token
        invalidatedTokenRepository.save( InvalidatedToken.builder().id(jit).expiryTime(expiryDate.toInstant()).build());

        // Generate new token
        String email = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        log.info("User {} refreshed token", email);
        return jwtService.generateTokenPair(user);
    }

    @Transactional
    public void logout(LogoutRequest request){
        try {
            SignedJWT signedToken = jwtService.verifyToken(request.getToken(), true);
            String jit = signedToken.getJWTClaimsSet().getJWTID();
            Date expiryDate = signedToken.getJWTClaimsSet().getExpirationTime();

            invalidatedTokenRepository.save(InvalidatedToken.builder().id(jit).expiryTime(expiryDate.toInstant()).build());
            log.info("User {} logged out", signedToken.getJWTClaimsSet().getSubject());
        }
        catch (AppException e) {
            if (e.getErrorCode() == ErrorCode.UNAUTHENTICATED) {
                log.info("Token already logged out");
            } else {
                throw e;
            }
        }
        catch(ParseException | JOSEException e){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    public void verifyEmail(String token) {
        emailVerificationService.verify(token);
    }

    public void resendVerification(ResendVerificationRequest request) {
        emailVerificationService.resend(request.getEmail());
    }

    public User processOAuth2User(String email, String name, String picture, String provider, String providerId) {
        return userRepository.findByEmail(email)
                .map(existingUser -> {
                    if (existingUser.getProvider() == null) {
                        existingUser.setProvider(provider);
                        existingUser.setProviderId(providerId);
                    }
                    if (existingUser.getAvatarUrl() == null && picture != null) {
                        existingUser.setAvatarUrl(picture);
                    }
                    if (!existingUser.getIsActive()) {
                        existingUser.setIsActive(true);
                    }
                    if (!Boolean.TRUE.equals(existingUser.getEmailVerified())) {
                        existingUser.setEmailVerified(true);
                    }
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    var userRole = roleRepository.findById("USER");
                    Set<Role> roles = new HashSet<>();
                    userRole.ifPresent(roles::add);

                    User newUser = User.builder()
                            .email(email)
                            .fullName(name)
                            .avatarUrl(picture)
                            .provider(provider)
                            .providerId(providerId)
                            .roles(roles)
                            .emailVerified(true)
                            .isActive(true)
                            .build();
                    return userRepository.save(newUser);
                });
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {

        User user = securityUtils.getCurrentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.NEW_PASSWORD_MUST_BE_DIFFERENT);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
        passwordResetTokenRepository.deleteByUser(user);
        invalidatedTokenRepository.deleteByUser(user);    
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {return;}
        User user = optionalUser.get();
        passwordResetTokenRepository.deleteByUser(user);
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
        passwordResetTokenRepository.save(passwordResetToken);
        mailService.sendPasswordResetEmail(user, token);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = passwordResetTokenRepository.findByTokenAndUsedFalse(request.getToken())
                .orElseThrow(() -> new AppException(ErrorCode.PASSWORD_RESET_TOKEN_INVALID));
        if (token.isExpired()) {
            throw new AppException(ErrorCode.PASSWORD_RESET_TOKEN_EXPIRED);
        }
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        token.setUsed(true);
        passwordResetTokenRepository.save(token);
        passwordResetTokenRepository.deleteByUser(user);
    }
}
