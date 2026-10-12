package com.corely.corely_backend.seed;

import com.corely.corely_backend.entity.Permission;
import com.corely.corely_backend.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
public class PermissionSeeder implements CommandLineRunner {
    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) {
        if (permissionRepository.count() == 0) {
            permissionRepository.saveAll(List.of(
                Permission.builder().name("MANAGE_USERS").description("Create, update, delete users").build(),
                Permission.builder().name("MANAGE_ROLES").description("Create, update, delete roles").build(),
                Permission.builder().name("MANAGE_PRODUCTS").description("Create, update, delete products").build(),
                Permission.builder().name("MANAGE_Order").description("Manage Order").build(),
                Permission.builder().name("VIEW_DASHBOARD").description("View dashboard statistics").build()
            ));
        }
    }
}
