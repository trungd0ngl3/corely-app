package com.corely.corely_backend.controller;

import com.corely.corely_backend.dto.request.auth.*;
import com.corely.corely_backend.dto.response.ApiResponse;
import com.corely.corely_backend.dto.response.auth.AuthenticateResponse;
import com.corely.corely_backend.dto.response.auth.IntrospectResponse;
import com.corely.corely_backend.dto.response.auth.UserResponse;
import com.corely.corely_backend.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthenticationService authenticationService;

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.success(authenticationService.register(request), "User registered successfully");
    }

    @PostMapping("/login")
    public ApiResponse<AuthenticateResponse> login(@RequestBody @Valid AuthenticateRequest request) {
        return ApiResponse.success(authenticationService.authenticate(request));
    }

    @PostMapping("/tokens/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody @Valid IntrospectRequest request) {
        return ApiResponse.success(authenticationService.introspect(request));
    }

    @PostMapping("/tokens/refresh")
    public ApiResponse<AuthenticateResponse> refresh(@RequestBody @Valid RefreshTokenRequest request)
            throws ParseException, JOSEException {
        return ApiResponse.success(authenticationService.refreshToken(request.getToken()));
    }

    @PostMapping("/tokens/logout")
    public ApiResponse<Void> logout(@RequestBody @Valid LogoutRequest request){
        authenticationService.logout(request);
        return ApiResponse.success("Logged out successfully");
    }

    @GetMapping("/verify-email")
    public ApiResponse<Void> verifyEmail(@RequestParam String token) {
        authenticationService.verifyEmail(token);
        return ApiResponse.success("Email verified successfully");
    }

    @PostMapping("/resend-verification")
    public ApiResponse<Void> resendVerification(@RequestBody @Valid ResendVerificationRequest request) {
        authenticationService.resendVerification(request);
        return ApiResponse.success("Verification email sent successfully");
     
    }
    @PatchMapping("/change-password")
    public ApiResponse<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        authenticationService.changePassword(request);

        return ApiResponse.success();
    }
    
    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);
        return ApiResponse.success();
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ApiResponse.success();
    }
}
