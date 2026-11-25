package com.corely.corely_backend.dto.response.order;

import com.corely.corely_backend.enums.OrderStatus;
import com.corely.corely_backend.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderResponse {
    UUID id;
    String orderCode;
    UUID userId;
    UUID storeId;
    String storeName;
    BigDecimal totalAmount;
    String shippingAddress;
    String shippingMethod;
    OrderStatus status;
    String paymentMethod;
    PaymentStatus paymentStatus;
    LocalDateTime createdAt;
    List<OrderItemResponse> items;
}
