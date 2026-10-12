package com.corely.corely_backend.service;

import com.corely.corely_backend.dto.request.auth.RoleRequest;
import com.corely.corely_backend.dto.response.auth.RoleResponse;
import com.corely.corely_backend.entity.Permission;
import com.corely.corely_backend.entity.Role;
import com.corely.corely_backend.mapper.RoleMapper;
import com.corely.corely_backend.repository.PermissionRepository;
import com.corely.corely_backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request){
        Role role = roleMapper.toRole(request);
        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public List<RoleResponse> getAll(){
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    public void delete(String name){
        roleRepository.deleteById(name);
    }

}
