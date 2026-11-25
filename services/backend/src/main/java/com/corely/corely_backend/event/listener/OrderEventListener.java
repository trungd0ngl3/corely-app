package com.corely.corely_backend.event.listener;

import com.corely.corely_backend.event.OrderCreatedEvent;
import com.corely.corely_backend.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderEventListener {

    CartService cartService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        for (var item : event.getCartItems()) {
            cartService.removeFromCart(item.getProductId(), item.getVariantId());
        }
    }
}