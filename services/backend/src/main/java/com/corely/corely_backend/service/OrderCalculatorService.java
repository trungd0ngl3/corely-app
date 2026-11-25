package com.corely.corely_backend.service;

import com.corely.corely_backend.entity.OrderItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderCalculatorService {

    public BigDecimal calculateSubtotal(List<OrderItem> items) {
        if (items == null) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotal(BigDecimal subtotal, BigDecimal shippingFee, BigDecimal discount) {
        if (subtotal == null) subtotal = BigDecimal.ZERO;
        if (shippingFee == null) shippingFee = BigDecimal.ZERO;
        if (discount == null) discount = BigDecimal.ZERO;
        return subtotal.add(shippingFee).subtract(discount);
    }
}
