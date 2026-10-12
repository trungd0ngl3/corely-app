package com.corely.corely_backend.dto.response.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    UUID id;
    String email;
    String fullName;
    String phone;
    LocalDate dateOfBirth;
    Set<String> roles;
    Boolean isActive;
    String avatarUrl;
}
