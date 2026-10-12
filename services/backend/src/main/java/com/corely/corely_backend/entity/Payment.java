package com.corely.corely_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String paymentMethod; // VNPAY, CREDIT_CARD, BANK_TRANSFER, etc.

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status; // PENDING, COMPLETED, FAILED, CANCELLED

    private String transactionId; // VNPay transaction ID or bank reference

    private String responseCode; // VNPay response code

    private String responseMessage; // VNPay response message

    private String bankCode;

    private String bankTranNo;

    private String payDate;

    private String checksum; // For VNPay verification

}
