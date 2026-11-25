package com.corely.corely_backend.dto.response.product;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ProductResponse {
    UUID id;
    String name;
    String slug;
    String sku;
    BigDecimal price;
    BigDecimal discountPrice;
    Integer stockQuantity;
    String description;
    Map<String, Object> specs;
    String thumbnailUrl;
    Boolean isActive;
    UUID storeId;
    String storeName;
    UUID categoryId;
    String categoryName;
    UUID brandId;
    String brandName;
    List<String> imageUrls;
    List<ProductVariantResponse> variants;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
