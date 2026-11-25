package com.corely.corely_backend.seed;

import com.corely.corely_backend.entity.Order;
import com.corely.corely_backend.enums.OrderStatus;
import com.corely.corely_backend.repository.OrderRepository;
import com.corely.corely_backend.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@org.springframework.core.annotation.Order(8)
@RequiredArgsConstructor
public class OrderSeeder implements CommandLineRunner {
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    @Override
    public void run(String... args) {
        if (orderRepository.count() == 0) {
            var store = storeRepository.findTopBy().orElseThrow();
            List<Order> orders = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                orders.add(Order.builder()
                    .orderCode("ORD" + (1000 + i))
                    .totalAmount(new BigDecimal("1000000"))
                    .status(OrderStatus.values()[i % OrderStatus.values().length])
                    .store(store)
                    .build());
            }
            orderRepository.saveAll(orders);
        }
    }
}
