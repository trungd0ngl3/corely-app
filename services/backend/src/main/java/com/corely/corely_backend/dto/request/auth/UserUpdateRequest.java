package com.corely.corely_backend.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Email(message = "EMAIL_INVALID")
    String email;

    @Size(min = 8, message = "PASSWORD_TOO_SHORT")
    String password;

    String fullName;

    LocalDate dateOfBirth;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "PHONE_INVALID")
    String phone;

    String avatarUrl;

    List<String> roles;
}
