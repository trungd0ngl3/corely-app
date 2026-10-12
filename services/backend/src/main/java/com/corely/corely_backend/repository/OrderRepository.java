package com.corely.corely_backend.repository;

import com.corely.corely_backend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<Order> findByStoreIdOrderByCreatedAtDesc(UUID storeId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = com.corely.corely_backend.enums.OrderStatus.DELIVERED")
    BigDecimal sumRevenueDelivered();

    long countByStatus(com.corely.corely_backend.enums.OrderStatus status);

    List<Order> findTop10ByOrderByCreatedAtDesc();

    java.util.Optional<Order> findByOrderCode(String orderCode);

    boolean existsByOrderCode(String orderCode);
}
