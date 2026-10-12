package com.corely.corely_backend.entity;

import com.corely.corely_backend.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "order_timelines")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTimeline extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    private OrderStatus fromStatus;

    @Enumerated(EnumType.STRING)
    private OrderStatus toStatus;

    private String changedBy;

    private String note;
}