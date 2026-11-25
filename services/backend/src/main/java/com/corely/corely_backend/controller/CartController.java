package com.corely.corely_backend.controller;

import com.corely.corely_backend.dto.request.cart.ApplyVoucherRequest;
import com.corely.corely_backend.dto.request.cart.CartItemRequest;
import com.corely.corely_backend.dto.response.ApiResponse;
import com.corely.corely_backend.dto.response.cart.CartItemResponse;
import com.corely.corely_backend.dto.response.cart.CartResponse;
import com.corely.corely_backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ApiResponse<CartResponse> getCart() {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCart())
                .build();
    }

    @PostMapping("/items")
    public ApiResponse<Void> addToCart(@RequestBody @Valid CartItemRequest request) {
        cartService.addToCart(request);
        return ApiResponse.<Void>builder().build();
    }

    @PutMapping("/items")
    public ApiResponse<Void> updateCartItem(@RequestBody @Valid CartItemRequest request) {
        cartService.updateCartItem(request);
        return ApiResponse.<Void>builder().build();
    }

    @DeleteMapping("/items/{productId}")
    public ApiResponse<Void> removeFromCart(
            @PathVariable UUID productId,
            @RequestParam(required = false) UUID variantId) {
        cartService.removeFromCart(productId, variantId);
        return ApiResponse.<Void>builder().build();
    }

    @DeleteMapping
    public ApiResponse<Void> clearCart() {
        cartService.clearCart();
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/voucher")
    public ApiResponse<CartResponse> applyVoucher(@RequestBody @Valid ApplyVoucherRequest request) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.applyVoucher(request.getVoucherCode()))
                .build();
    }

    @DeleteMapping("/voucher")
    public ApiResponse<CartResponse> removeVoucher() {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.removeVoucher())
                .build();
    }

    @GetMapping("/stock-check")
    public ApiResponse<List<CartItemResponse>> checkStock() {
        return ApiResponse.<List<CartItemResponse>>builder()
                .result(cartService.checkStock())
                .build();
    }
}
