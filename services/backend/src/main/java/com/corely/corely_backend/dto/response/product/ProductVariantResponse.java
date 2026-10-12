package com.corely.corely_backend.dto.response.product;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ProductVariantResponse {
    UUID id;
    String name;
    String sku;
    BigDecimal price;
    Integer stockQuantity;
    String imageUrl;
    Boolean isActive;
    LocalDateTime createdAt;
}
