package com.corely.corely_backend.seed;

import com.corely.corely_backend.entity.Store;
import com.corely.corely_backend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
@RequiredArgsConstructor
public class StoreSeeder implements CommandLineRunner {
    private final StoreRepository storeRepository;

    @Override
    public void run(String... args) {
        if (storeRepository.count() == 0) {
            storeRepository.save(Store.builder()
                .name("Corely Official Store")
                .slug("corely-official-store")
                .description("Official Store")
                .isActive(true)
                .isVerified(true)
                .rating(5.0)
                .build());
        }
    }
}
