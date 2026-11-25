package com.corely.corely_backend.mapper;

import com.corely.corely_backend.dto.response.payment.PaymentResponse;
import com.corely.corely_backend.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .responseCode(payment.getResponseCode())
                .responseMessage(payment.getResponseMessage())
                .bankCode(payment.getBankCode())
                .bankTranNo(payment.getBankTranNo())
                .payDate(payment.getPayDate())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
