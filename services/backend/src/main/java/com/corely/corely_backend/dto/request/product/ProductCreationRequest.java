package com.corely.corely_backend.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ProductCreationRequest {
    @NotBlank(message = "PRODUCT_NAME_REQUIRED")
    String name;

    @NotBlank(message = "PRODUCT_SKU_REQUIRED")
    String sku;

    @NotNull(message = "PRODUCT_PRICE_REQUIRED")
    @jakarta.validation.constraints.Positive(message = "PRODUCT_PRICE_POSITIVE")
    BigDecimal price;

    @jakarta.validation.constraints.PositiveOrZero(message = "PRODUCT_DISCOUNT_PRICE_POSITIVE_OR_ZERO")
    BigDecimal discountPrice;

    @jakarta.validation.constraints.PositiveOrZero(message = "PRODUCT_STOCK_POSITIVE_OR_ZERO")
    Integer stockQuantity;

    String description;

    Map<String, Object> specs; // dynamic specs

    String thumbnailUrl;

    @NotNull(message = "STORE_ID_REQUIRED")
    UUID storeId;

    @NotNull(message = "CATEGORY_ID_REQUIRED")
    UUID categoryId;

    @NotNull(message = "BRAND_ID_REQUIRED")
    UUID brandId;

    List<String> imageUrls;

    List<ProductVariantRequest> variants;
}
