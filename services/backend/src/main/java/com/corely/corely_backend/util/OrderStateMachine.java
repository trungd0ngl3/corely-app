package com.corely.corely_backend.util;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.corely.corely_backend.enums.OrderStatus;
import com.corely.corely_backend.exception.AppException;
import com.corely.corely_backend.exception.ErrorCode;

@Component
public class OrderStateMachine {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
            OrderStatus.PENDING_PAYMENT,
            Set.of(
                    OrderStatus.AWAITING_CONFIRMATION,
                    OrderStatus.CANCELLED
            ),

            OrderStatus.AWAITING_CONFIRMATION,
            Set.of(
                    OrderStatus.PROCESSING,
                    OrderStatus.CANCELLED
            ),

            OrderStatus.PROCESSING,
            Set.of(
                    OrderStatus.SHIPPING
            ),

            OrderStatus.SHIPPING,
            Set.of(
                    OrderStatus.DELIVERED
            ),

            OrderStatus.DELIVERED,
            Set.of(
                    OrderStatus.COMPLETED,
                    OrderStatus.REFUNDED
            ),

            OrderStatus.COMPLETED,
            Set.of(),

            OrderStatus.CANCELLED,
            Set.of(),

            OrderStatus.REFUNDED,
            Set.of()
    );

    private static final Map<OrderStatus, Set<OrderStatus>> SELLER_ALLOWED = Map.of(
            OrderStatus.AWAITING_CONFIRMATION,
            Set.of(
                    OrderStatus.PROCESSING,
                    OrderStatus.CANCELLED
            ),

            OrderStatus.PROCESSING,
            Set.of(
                    OrderStatus.SHIPPING
            ),

            OrderStatus.SHIPPING,
            Set.of(
                    OrderStatus.DELIVERED
            )
    );

    private static final Map<OrderStatus, Set<OrderStatus>> BUYER_ALLOWED = Map.of(
            OrderStatus.PENDING_PAYMENT,
            Set.of(
                    OrderStatus.AWAITING_CONFIRMATION,
                    OrderStatus.CANCELLED
            ),

            OrderStatus.AWAITING_CONFIRMATION,
            Set.of(
                    OrderStatus.CANCELLED
            ),

            OrderStatus.DELIVERED,
            Set.of(
                    OrderStatus.COMPLETED,
                    OrderStatus.REFUNDED
            )
    );

    public boolean canTransition( OrderStatus from, OrderStatus to  ) {
        return ALLOWED.getOrDefault(from, Set.of()).contains(to);
    }

    public void validate(OrderStatus from, OrderStatus to){
        if(!canTransition(from, to)){
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }
    }

    public boolean canSellerTransition(OrderStatus from, OrderStatus to) {
        return SELLER_ALLOWED.getOrDefault(from, Set.of()).contains(to);
    }

    public boolean canBuyerTransition(OrderStatus from, OrderStatus to) {
        return BUYER_ALLOWED.getOrDefault(from, Set.of()).contains(to);
    }

    public void validateSellerTransition(OrderStatus from, OrderStatus to) {
        if (!canSellerTransition(from, to)) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }
    }

    public void validateBuyerTransition(OrderStatus from, OrderStatus to) {
        if (!canBuyerTransition(from, to)) {
            throw new AppException(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
        }
    }
}
