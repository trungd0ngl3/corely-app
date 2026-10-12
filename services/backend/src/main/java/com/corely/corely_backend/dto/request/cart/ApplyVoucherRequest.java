package com.corely.corely_backend.dto.request.cart;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ApplyVoucherRequest {
    @NotBlank(message = "Voucher code is required")
    String voucherCode;
}
