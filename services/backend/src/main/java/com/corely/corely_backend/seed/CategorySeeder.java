package com.corely.corely_backend.seed;

import com.corely.corely_backend.entity.Category;
import com.corely.corely_backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(6)
@RequiredArgsConstructor
public class CategorySeeder implements CommandLineRunner {
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (!categoryRepository.existsBySlug("cpu")) {
            categoryRepository.saveAll(List.of(
                Category.builder().name("CPU").slug("cpu").build(),
                Category.builder().name("Graphics Card").slug("graphics-card").build(),
                Category.builder().name("Motherboard").slug("motherboard").build(),
                Category.builder().name("RAM").slug("ram").build(),
                Category.builder().name("SSD").slug("ssd").build(),
                Category.builder().name("Laptop").slug("laptop").build()
            ));
        }
    }
}
