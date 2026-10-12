package com.corely.corely_backend.seed;

import com.corely.corely_backend.entity.Permission;
import com.corely.corely_backend.entity.Role;
import com.corely.corely_backend.repository.PermissionRepository;
import com.corely.corely_backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@Order(2)
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) {

        var allPermissions = new HashSet<>(permissionRepository.findAll());

        if (!roleRepository.existsById("ADMIN")) {
            roleRepository.save(
                    Role.builder()
                            .name("ADMIN")
                            .description("Admin role")
                            .permissions(allPermissions)
                            .build());
        }

        if (!roleRepository.existsById("USER")) {
            roleRepository.save(
                    Role.builder()
                            .name("USER")
                            .description("User role")
                            .permissions(new HashSet<>())
                            .build());
        }

        if (!roleRepository.existsById("STAFF")) {
            roleRepository.save(
                    Role.builder()
                            .name("STAFF")
                            .description("Staff role")
                            .permissions(allPermissions)
                            .build());
        }
    }
}
