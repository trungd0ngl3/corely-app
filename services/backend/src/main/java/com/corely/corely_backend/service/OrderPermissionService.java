package com.corely.corely_backend.service;

import com.corely.corely_backend.entity.Order;
import com.corely.corely_backend.enums.OrderStatus;
import com.corely.corely_backend.util.OrderStateMachine;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderPermissionService {

    OrderStateMachine orderStateMachine;

    public void validateTransition(Order order, OrderStatus newStatus, boolean isSeller) {
        if (isSeller) {
            orderStateMachine.validateSellerTransition(order.getStatus(), newStatus);
        } else {
            orderStateMachine.validateBuyerTransition(order.getStatus(), newStatus);
        }
    }
}