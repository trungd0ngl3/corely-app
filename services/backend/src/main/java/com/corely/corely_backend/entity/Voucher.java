package com.corely.corely_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Voucher extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code;

    private String description;

    // PERCENTAGE, FIXED, FREE_SHIP
    private String discountType;

    private BigDecimal discountValue;

    private BigDecimal minOrderValue;

    private BigDecimal maxDiscountAmount;

    private Integer usageLimit;

    @Builder.Default
    private Integer usedCount = 0;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Builder.Default
    private Boolean isActive = true;

    // If store is null, it's a global voucher
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;
}
