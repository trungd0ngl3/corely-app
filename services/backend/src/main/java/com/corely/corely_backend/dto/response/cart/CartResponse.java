package com.corely.corely_backend.dto.response.cart;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.Map;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CartResponse {
    UUID userId;
    List<CartItemResponse> items;

    // Group items by store for checkout convenience
    Map<UUID, List<CartItemResponse>> itemsByStore;

    BigDecimal totalAmount;
    Integer totalItems;

    // Voucher/discount
    String voucherCode;
    BigDecimal discountAmount;
    BigDecimal finalAmount;
}
