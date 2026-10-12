package com.corely.corely_backend.dto.request.auth;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PermissionRequest {
    String name;
    String description;
}
