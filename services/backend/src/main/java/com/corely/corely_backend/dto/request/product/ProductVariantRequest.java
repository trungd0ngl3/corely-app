package com.corely.corely_backend.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantRequest {

    UUID id;

    @NotBlank(message = "VARIANT_NAME_REQUIRED")
    String name;

    @NotBlank(message = "VARIANT_SKU_REQUIRED")
    String sku;

    @NotNull(message = "VARIANT_PRICE_REQUIRED")
    @PositiveOrZero(message = "VARIANT_PRICE_INVALID")
    BigDecimal price;

    @PositiveOrZero(message = "VARIANT_STOCK_INVALID")
    Integer stockQuantity;

    String imageUrl;
}
