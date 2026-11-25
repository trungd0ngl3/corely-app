package com.corely.corely_backend.service;

import com.corely.corely_backend.dto.request.payment.VNPayCallbackRequest;
import com.corely.corely_backend.dto.response.payment.PaymentResponse;
import com.corely.corely_backend.entity.Order;
import com.corely.corely_backend.entity.Payment;
import com.corely.corely_backend.enums.PaymentStatus;
import com.corely.corely_backend.exception.AppException;
import com.corely.corely_backend.exception.ErrorCode;
import com.corely.corely_backend.mapper.PaymentMapper;
import com.corely.corely_backend.repository.OrderRepository;
import com.corely.corely_backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Value("${vnpay.tmnCode:TEST}")
    private String vnpayTmnCode;

    @Value("${vnpay.hashSecret:TESTSECRET}")
    private String vnpayHashSecret;

    @Value("${vnpay.payUrl:https://sandbox.vnpayment.vn/paygate/pay.html}")
    private String vnpayPayUrl;

    @Value("${vnpay.apiUrl:https://api.vnpayment.vn}")
    private String vnpayApiUrl;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;

    @Transactional
    public String createVNPayPaymentUrl(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod("VNPAY")
                .amount(order.getTotalAmount())
                .status("PENDING")
                .build();

        payment = paymentRepository.save(payment);

        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnpayTmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(order.getTotalAmount().multiply(new BigDecimal(100)).longValue()));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", payment.getId().toString());
        vnpParams.put("vnp_OrderInfo", "Payment for order " + orderId);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", frontendUrl + "/payment-result");
        vnpParams.put("vnp_IpAddr", "127.0.0.1");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC+7"));
        String createDate = formatter.format(new Date());
        vnpParams.put("vnp_CreateDate", createDate);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+7"));
        calendar.add(Calendar.MINUTE, 15);
        String expireDate = formatter.format(calendar.getTime());
        vnpParams.put("vnp_ExpireDate", expireDate);

        String queryUrl = buildQueryString(vnpParams);
        String secureHash = generateSecureHash(queryUrl);
        vnpParams.put("vnp_SecureHash", secureHash);

        String paymentUrl = vnpayPayUrl + "?" + buildQueryString(vnpParams);
        log.info("Generated VNPay URL for order: {}", orderId);
        return paymentUrl;
    }

    @Transactional
    public PaymentResponse processVNPayCallback(VNPayCallbackRequest request) {
        log.info("Processing VNPay callback for transaction: {}", request.getVnp_TxnRef());

        // Verify checksum
        Map<String, String> fields = new TreeMap<>();
        fields.put("vnp_Amount", request.getVnp_Amount());
        fields.put("vnp_BankCode", request.getVnp_BankCode());
        fields.put("vnp_BankTranNo", request.getVnp_BankTranNo());
        fields.put("vnp_CardType", request.getVnp_CardType());
        fields.put("vnp_OrderInfo", request.getVnp_OrderInfo());
        fields.put("vnp_PayDate", request.getVnp_PayDate());
        fields.put("vnp_ResponseCode", request.getVnp_ResponseCode());
        fields.put("vnp_TmnCode", request.getVnp_TmnCode());
        fields.put("vnp_TransactionNo", request.getVnp_TransactionNo());
        fields.put("vnp_TransactionStatus", request.getVnp_TransactionStatus());
        fields.put("vnp_TxnRef", request.getVnp_TxnRef());

        String calculatedHash = generateSecureHash(buildQueryString(fields));
        if (!calculatedHash.equals(request.getVnp_SecureHash())) {
            throw new AppException(ErrorCode.INVALID_SIGNATURE);
        }

        Payment payment = paymentRepository.findByTransactionId(request.getVnp_TxnRef())
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        payment.setTransactionId(request.getVnp_TransactionNo());
        payment.setResponseCode(request.getVnp_ResponseCode());
        payment.setResponseMessage(getVNPayResponseMessage(request.getVnp_ResponseCode()));
        payment.setBankCode(request.getVnp_BankCode());
        payment.setBankTranNo(request.getVnp_BankTranNo());
        payment.setPayDate(request.getVnp_PayDate());
        payment.setChecksum(request.getVnp_SecureHash());

        if ("00".equals(request.getVnp_ResponseCode())) {
            payment.setStatus("COMPLETED");
            Order order = payment.getOrder();
            order.setPaymentStatus(PaymentStatus.PAID);
            orderRepository.save(order);
        } else {
            payment.setStatus("FAILED");
        }

        payment = paymentRepository.save(payment);
        log.info("Callback processed for transaction: {}", request.getVnp_TxnRef());

        return paymentMapper.toResponse(payment);
    }

    private String generateSecureHash(String data) {
        try {
            Mac hmacSha512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    vnpayHashSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512");
            hmacSha512.init(secretKeySpec);
            byte[] hash = hmacSha512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return toHexString(hash);
        } catch (Exception e) {
            log.error("Error generating secure hash", e);
            throw new RuntimeException(e);
        }
    }

    private String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String buildQueryString(Map<String, String> params) throws RuntimeException {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                sb.append("&");
            }
            try {
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue() != null ? entry.getValue() : "", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            first = false;
        }
        return sb.toString();
    }

    private String getVNPayResponseMessage(String responseCode) {
        return switch (responseCode) {
            case "00" -> "Transaction Successful";
            case "01" -> "User canceled transaction";
            case "02" -> "Transaction failed";
            case "04" -> "Transaction rejected by bank";
            case "05" -> "Balance insufficient";
            case "06" -> "Transaction suspended";
            case "07" -> "Transaction exception";
            case "09" -> "Card/Account not yet activated";
            case "10" -> "Authentication failed";
            case "11" -> "Amount exceeded limit";
            case "12" -> "Merchant maintenance";
            case "13" -> "Merchant locked";
            case "21" -> "Transaction suspected";
            case "99" -> "Unknown error";
            default -> "Unknown response code: " + responseCode;
        };
    }

    public PaymentResponse getPaymentByOrderId(UUID orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
        return paymentMapper.toResponse(payment);
    }
}
