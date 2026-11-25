package com.corely.corely_backend.mapper;

import com.corely.corely_backend.dto.request.auth.PermissionRequest;
import com.corely.corely_backend.dto.response.auth.PermissionResponse;
import com.corely.corely_backend.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
