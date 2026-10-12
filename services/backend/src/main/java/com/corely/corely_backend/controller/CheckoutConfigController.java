package com.corely.corely_backend.controller;

import com.corely.corely_backend.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/checkout-config")
public class CheckoutConfigController {

    @GetMapping("/shipping-methods")
    public ApiResponse<List<String>> getShippingMethods() {
        return ApiResponse.<List<String>>builder()
                .result(Arrays.asList("STANDARD", "EXPRESS", "ECONOMY"))
                .build();
    }

    @GetMapping("/payment-methods")
    public ApiResponse<List<String>> getPaymentMethods() {
        return ApiResponse.<List<String>>builder()
                .result(Arrays.asList("COD", "VNPAY", "MOMO"))
                .build();
    }
}
