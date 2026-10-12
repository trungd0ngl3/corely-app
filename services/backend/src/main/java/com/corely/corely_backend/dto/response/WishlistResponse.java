package com.corely.corely_backend.dto.response;

import com.corely.corely_backend.dto.response.product.ProductResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WishlistResponse {
    UUID id;
    List<WishlistItemResponse> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class WishlistItemResponse {
        ProductResponse product;
        LocalDateTime createdAt;
    }
}
