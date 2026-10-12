package com.corely.corely_backend.entity;

import com.corely.corely_backend.enums.OrderStatus;
import com.corely.corely_backend.enums.PaymentMethod;
import com.corely.corely_backend.enums.PaymentStatus;
import com.corely.corely_backend.enums.ShippingMethod;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private BigDecimal subtotal;
    private BigDecimal shippingFee;
    private BigDecimal discount;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private PaymentMethod paymentMethod;

    @Column(unique = true)
    private String orderCode;

    @Column(columnDefinition = "TEXT")
    private String shippingAddress;
    
    private ShippingMethod shippingMethod;

    @ManyToOne
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
}
