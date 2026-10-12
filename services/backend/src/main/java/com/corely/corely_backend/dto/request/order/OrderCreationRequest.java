package com.corely.corely_backend.dto.request.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import com.corely.corely_backend.enums.PaymentMethod;
import com.corely.corely_backend.enums.ShippingMethod;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderCreationRequest {

    @NotNull(message = "STORE_ID_REQUIRED")
    UUID storeId;

    @NotBlank(message = "SHIPPING_ADDRESS_REQUIRED")
    String shippingAddress;

    @NotBlank(message = "SHIPPING_METHOD_REQUIRED")
    ShippingMethod shippingMethod; // e.g., "STANDARD", "EXPRESS"

    @NotBlank(message = "PAYMENT_METHOD_REQUIRED")
    PaymentMethod paymentMethod; // e.g., "COD", "VNPAY"
}
