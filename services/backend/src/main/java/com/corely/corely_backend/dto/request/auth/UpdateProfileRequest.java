package com.corely.corely_backend.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileRequest {

    @NotBlank(message = "FULL_NAME_REQUIRED")
    String fullName;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "INVALID_PHONE_NUMBER")
    String phone;

    String avatarUrl;

    @Past(message = "DATE_OF_BIRTH_MUST_BE_PAST")
    LocalDate dateOfBirth;
}
