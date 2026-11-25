package com.corely.corely_backend.service;

import com.corely.corely_backend.dto.request.auth.PermissionRequest;
import com.corely.corely_backend.dto.response.auth.PermissionResponse;
import com.corely.corely_backend.entity.Permission;
import com.corely.corely_backend.mapper.PermissionMapper;
import com.corely.corely_backend.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request){
        Permission permission = permissionMapper.toPermission(request);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    public List<PermissionResponse> getAll(){
        // lấy toàn bộ permission từ DB
        return permissionRepository.findAll()
                // chuyển List các Permission thành stream để xử lý
                .stream()
                // map mỗi Permission thành PermissionResponse sử dụng permissionMapper
                .map(permissionMapper::toPermissionResponse)
                // chuyển lại thành List<PermissionResponse>
                .toList();
    }

    public void delete(String name){
        permissionRepository.deleteById(name);
    }
}
