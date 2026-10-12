package com.corely.corely_backend.event;

import com.corely.corely_backend.dto.response.cart.CartItemResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class OrderCreatedEvent {
    private final List<CartItemResponse> cartItems;
}