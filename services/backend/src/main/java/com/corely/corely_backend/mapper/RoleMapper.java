package com.corely.corely_backend.mapper;

import com.corely.corely_backend.dto.request.auth.RoleRequest;
import com.corely.corely_backend.dto.response.auth.RoleResponse;
import com.corely.corely_backend.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @org.mapstruct.Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    @org.mapstruct.Mapping(target = "permissions", ignore = true)
    RoleResponse toRoleResponse(Role role);
}
