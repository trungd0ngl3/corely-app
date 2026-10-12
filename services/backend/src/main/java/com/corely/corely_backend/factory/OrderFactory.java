package com.corely.corely_backend.factory;

import com.corely.corely_backend.dto.request.order.OrderCreationRequest;
import com.corely.corely_backend.entity.Order;
import com.corely.corely_backend.entity.Store;
import com.corely.corely_backend.entity.User;
import com.corely.corely_backend.enums.OrderStatus;
import com.corely.corely_backend.enums.PaymentMethod;
import com.corely.corely_backend.enums.PaymentStatus;
import com.corely.corely_backend.util.OrderCodeGenerator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderFactory {

    OrderCodeGenerator orderCodeGenerator;

    public Order create(OrderCreationRequest request, User user, Store store) {
        OrderStatus initialStatus = request.getPaymentMethod() == PaymentMethod.COD
                ? OrderStatus.AWAITING_CONFIRMATION
                : OrderStatus.PENDING_PAYMENT;

        return Order.builder()
                .user(user)
                .store(store)
                .status(initialStatus)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.UNPAID)
                .orderCode(orderCodeGenerator.generate())
                .shippingAddress(request.getShippingAddress())
                .subtotal(BigDecimal.ZERO)
                .shippingFee(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .build();
    }

    public void finalizeOrder(Order order, java.util.List<com.corely.corely_backend.entity.OrderItem> items, BigDecimal subtotal, BigDecimal shippingFee, BigDecimal discount, BigDecimal totalAmount) {
        order.setItems(items);
        order.setSubtotal(subtotal);
        order.setShippingFee(shippingFee);
        order.setDiscount(discount);
        order.setTotalAmount(totalAmount);
        for (var item : items) {
            item.setOrder(order);
        }
    }
}
