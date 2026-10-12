package com.corely.corely_backend.dto.response.auth;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;
@Builder
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RoleResponse {
    String name;
    String description;

    Set<PermissionResponse> permissions;
}
