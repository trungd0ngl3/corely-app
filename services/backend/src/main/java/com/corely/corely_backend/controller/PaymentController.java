package com.corely.corely_backend.controller;

import com.corely.corely_backend.dto.request.payment.VNPayCallbackRequest;
import com.corely.corely_backend.dto.response.payment.PaymentResponse;
import com.corely.corely_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}/vnpay-url")
    public ResponseEntity<String> createVNPayUrl(@PathVariable UUID orderId) {
        log.info("Creating VNPay URL for order: {}", orderId);
        String paymentUrl = paymentService.createVNPayPaymentUrl(orderId);
        return ResponseEntity.ok(paymentUrl);
    }

    @GetMapping("/vnpay/callback")
    public ResponseEntity<PaymentResponse> handleVNPayCallback(
            @RequestParam String vnp_Amount,
            @RequestParam String vnp_BankCode,
            @RequestParam String vnp_BankTranNo,
            @RequestParam String vnp_CardType,
            @RequestParam String vnp_OrderInfo,
            @RequestParam String vnp_PayDate,
            @RequestParam String vnp_ResponseCode,
            @RequestParam String vnp_TmnCode,
            @RequestParam String vnp_TransactionNo,
            @RequestParam String vnp_TransactionStatus,
            @RequestParam String vnp_TxnRef,
            @RequestParam String vnp_SecureHash) {

        log.info("Received VNPay callback for transaction: {}", vnp_TxnRef);

        VNPayCallbackRequest request = VNPayCallbackRequest.builder()
                .vnp_Amount(vnp_Amount)
                .vnp_BankCode(vnp_BankCode)
                .vnp_BankTranNo(vnp_BankTranNo)
                .vnp_CardType(vnp_CardType)
                .vnp_OrderInfo(vnp_OrderInfo)
                .vnp_PayDate(vnp_PayDate)
                .vnp_ResponseCode(vnp_ResponseCode)
                .vnp_TmnCode(vnp_TmnCode)
                .vnp_TransactionNo(vnp_TransactionNo)
                .vnp_TransactionStatus(vnp_TransactionStatus)
                .vnp_TxnRef(vnp_TxnRef)
                .vnp_SecureHash(vnp_SecureHash)
                .build();

        PaymentResponse response = paymentService.processVNPayCallback(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable UUID orderId) {
        log.info("Getting payment for order: {}", orderId);
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }
}
