package com.corely.corely_backend.dto.response.order;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderItemResponse {
    UUID id;
    UUID productId;
    UUID variantId;
    String productName;
    String variantName;
    String imageUrl;
    BigDecimal price;
    Integer quantity;
    BigDecimal subTotal;
}
