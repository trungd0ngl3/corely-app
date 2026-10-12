package com.corely.corely_backend.dto.response.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class IntrospectResponse {
    boolean isValid;
}
