package com.corely.corely_backend.dto.request.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CartItemRequest {
    @NotNull(message = "PRODUCT_ID_REQUIRED")
    UUID productId;

    UUID variantId; // Can be null if product has no variants

    @Min(value = 1, message = "QUANTITY_MIN_1")
    Integer quantity;
}
