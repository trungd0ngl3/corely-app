package com.corely.corely_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String name; // e.g. "8GB RAM - 256GB SSD"

    @Column(unique = true)
    private String sku;

    private BigDecimal price;

    private Integer stockQuantity;

    private String imageUrl;

    private Boolean isActive = true;
}
