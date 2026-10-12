package com.corely.corely_backend.repository;

import com.corely.corely_backend.entity.OrderTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderTimelineRepository extends JpaRepository<OrderTimeline, UUID> {
    List<OrderTimeline> findByOrderIdOrderByCreatedAtAsc(UUID orderId);
}