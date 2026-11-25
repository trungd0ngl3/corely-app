package com.corely.corely_backend.seed;

import com.corely.corely_backend.entity.Brand;
import com.corely.corely_backend.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(5)
@RequiredArgsConstructor
public class BrandSeeder implements CommandLineRunner {
    private final BrandRepository brandRepository;

    @Override
    public void run(String... args) {
        if (!brandRepository.existsBySlug("intel")) {
            brandRepository.saveAll(List.of(
                Brand.builder().name("Intel").slug("intel").build(),
                Brand.builder().name("AMD").slug("amd").build(),
                Brand.builder().name("NVIDIA").slug("nvidia").build(),
                Brand.builder().name("ASUS").slug("asus").build(),
                Brand.builder().name("MSI").slug("msi").build(),
                Brand.builder().name("Corsair").slug("corsair").build(),
                Brand.builder().name("Samsung").slug("samsung").build()
            ));
        }
    }
}
