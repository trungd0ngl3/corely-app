package com.corely.corely_backend.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordRequest {

    @NotBlank(message = "TOKEN_REQUIRED")
    String token;

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 8, max = 100)
    String newPassword;

}