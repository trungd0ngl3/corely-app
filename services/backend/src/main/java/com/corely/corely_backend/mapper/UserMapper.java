package com.corely.corely_backend.mapper;

import com.corely.corely_backend.dto.request.auth.UpdateProfileRequest;
import com.corely.corely_backend.dto.request.auth.UserCreationRequest;
import com.corely.corely_backend.dto.request.auth.UserUpdateRequest;
import com.corely.corely_backend.dto.response.auth.UserResponse;
import com.corely.corely_backend.entity.Role;
import com.corely.corely_backend.entity.User;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreationRequest request);

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream().map(role -> role.getName()).collect(Collectors.toSet());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void updateProfile(@MappingTarget User user, UpdateProfileRequest request);
}
