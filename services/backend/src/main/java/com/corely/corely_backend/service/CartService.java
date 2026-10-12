package com.corely.corely_backend.service;

import com.corely.corely_backend.dto.request.cart.CartItemRequest;
import com.corely.corely_backend.dto.response.cart.CartItemResponse;
import com.corely.corely_backend.dto.response.cart.CartResponse;
import com.corely.corely_backend.entity.Product;
import com.corely.corely_backend.entity.ProductVariant;
import com.corely.corely_backend.entity.Voucher;
import com.corely.corely_backend.exception.AppException;
import com.corely.corely_backend.exception.ErrorCode;
import com.corely.corely_backend.mapper.CartMapper;
import com.corely.corely_backend.repository.ProductRepository;
import com.corely.corely_backend.repository.ProductVariantRepository;
import com.corely.corely_backend.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

    RedisTemplate<String, Object> redisTemplate;
    ProductRepository productRepository;
    ProductVariantRepository productVariantRepository;
    VoucherRepository voucherRepository;
    UserService userService;
    CartMapper cartMapper;

    static final String CART_PREFIX = "cart:";
    static final String VOUCHER_PREFIX = "cart_voucher:";

    public void addToCart(CartItemRequest request) {
        String userId = getCurrentUserId();
        String cartKey = CART_PREFIX + userId;
        String itemKey = generateItemKey(request.getProductId(), request.getVariantId());

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getIsActive() || !product.getStore().getIsActive())
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);

        if (!product.getVariants().isEmpty() && request.getVariantId() == null)
            throw new AppException(ErrorCode.VARIANT_REQUIRED);

        ProductVariant variant = null;
        if (request.getVariantId() != null) {
            variant = productVariantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            if (!variant.getProduct().getId().equals(product.getId()))
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        int stock = getStock(product, variant);
        if (stock <= 0)
            throw new AppException(ErrorCode.OUT_OF_STOCK);

        HashOperations<String, String, Integer> hashOps = redisTemplate.opsForHash();
        Integer current = hashOps.get(cartKey, itemKey);
        int newQty = (current != null ? current : 0) + request.getQuantity();

        if (newQty > stock)
            throw new AppException(ErrorCode.OUT_OF_STOCK);

        hashOps.put(cartKey, itemKey, newQty);
        redisTemplate.expire(cartKey, java.time.Duration.ofDays(30));
    }

    public void updateCartItem(CartItemRequest request) {
        String userId = getCurrentUserId();
        String cartKey = CART_PREFIX + userId;
        String itemKey = generateItemKey(request.getProductId(), request.getVariantId());

        HashOperations<String, String, Integer> hashOps = redisTemplate.opsForHash();
        if (!hashOps.hasKey(cartKey, itemKey))
            throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);

        if (request.getQuantity() <= 0) {
            hashOps.delete(cartKey, itemKey);
            return;
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getIsActive() || !product.getStore().getIsActive())
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);

        ProductVariant variant = null;
        if (request.getVariantId() != null) {
            variant = productVariantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            if (!variant.getProduct().getId().equals(product.getId()))
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        int stock = getStock(product, variant);
        if (request.getQuantity() > stock)
            throw new AppException(ErrorCode.OUT_OF_STOCK);

        hashOps.put(cartKey, itemKey, request.getQuantity());
    }

    public void removeFromCart(UUID productId, UUID variantId) {
        String userId = getCurrentUserId();
        String cartKey = CART_PREFIX + userId;
        String itemKey = generateItemKey(productId, variantId);
        redisTemplate.opsForHash().delete(cartKey, itemKey);
    }

    public void clearCart() {
        String userId = getCurrentUserId();
        redisTemplate.delete(CART_PREFIX + userId);
        redisTemplate.delete(VOUCHER_PREFIX + userId);
    }

    public CartResponse getCart() {
        String userId = getCurrentUserId();
        String cartKey = CART_PREFIX + userId;

        HashOperations<String, String, Integer> hashOps = redisTemplate.opsForHash();
        Map<String, Integer> cartItems = hashOps.entries(cartKey);

        Set<UUID> productIds = new HashSet<>();
        Set<UUID> variantIds = new HashSet<>();
        for (String key : cartItems.keySet()) {
            String[] ids = key.split(":");
            productIds.add(UUID.fromString(ids[0]));
            if (!ids[1].equals("0"))
                variantIds.add(UUID.fromString(ids[1]));
        }

        Map<UUID, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        Map<UUID, ProductVariant> variantMap = productVariantRepository.findAllById(variantIds).stream()
                .collect(Collectors.toMap(ProductVariant::getId, v -> v));

        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalItems = 0;

        for (Map.Entry<String, Integer> entry : cartItems.entrySet()) {
            String[] ids = entry.getKey().split(":");
            UUID productId = UUID.fromString(ids[0]);
            UUID variantId = !ids[1].equals("0") ? UUID.fromString(ids[1]) : null;
            Integer quantity = entry.getValue();

            Product product = productMap.get(productId);
            if (product == null || !product.getIsActive() || !product.getStore().getIsActive()) {
                hashOps.delete(cartKey, entry.getKey());
                continue;
            }

            ProductVariant variant = variantId != null ? variantMap.get(variantId) : null;
            if (variantId != null && (variant == null || !variant.getProduct().getId().equals(product.getId()))) {
                hashOps.delete(cartKey, entry.getKey());
                continue;
            }

            CartItemResponse itemResponse = cartMapper.toResponse(product, variant, quantity);

            itemResponses.add(itemResponse);
            totalAmount = totalAmount.add(itemResponse.getSubtotal());
            totalItems += quantity;
        }

        Map<UUID, List<CartItemResponse>> itemsByStore = itemResponses.stream()
                .collect(Collectors.groupingBy(CartItemResponse::getStoreId));

        String voucherCode = (String) redisTemplate.opsForValue().get(VOUCHER_PREFIX + userId);
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (voucherCode != null) {
            try {
                discountAmount = calculateDiscount(voucherCode, totalAmount);
            } catch (AppException e) {
                // Voucher no longer valid, remove it
                redisTemplate.delete(VOUCHER_PREFIX + userId);
                voucherCode = null;
            }
        }

        BigDecimal finalAmount = totalAmount.subtract(discountAmount).max(BigDecimal.ZERO);

        return CartResponse.builder()
                .userId(UUID.fromString(userId))
                .items(itemResponses)
                .itemsByStore(itemsByStore)
                .totalAmount(totalAmount)
                .totalItems(totalItems)
                .voucherCode(voucherCode)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .build();
    }

    public CartResponse applyVoucher(String voucherCode) {
        String userId = getCurrentUserId();
        if (redisTemplate.opsForHash().entries(CART_PREFIX + userId).isEmpty())
            throw new AppException(ErrorCode.CART_EMPTY);

        Voucher voucher = validateVoucher(voucherCode);
        BigDecimal totalAmount = getCart().getTotalAmount();
        if (voucher.getMinOrderValue() != null && totalAmount.compareTo(voucher.getMinOrderValue()) < 0)
            throw new AppException(ErrorCode.INVALID_VOUCHER);

        redisTemplate.opsForValue().set(VOUCHER_PREFIX + userId, voucherCode, java.time.Duration.ofDays(30));

        return getCart();
    }

    public CartResponse removeVoucher() {
        String userId = getCurrentUserId();
        redisTemplate.delete(VOUCHER_PREFIX + userId);
        return getCart();
    }

    public List<CartItemResponse> checkStock() {
        CartResponse cart = getCart();
        return cart.getItems().stream()
                .filter(item -> !item.getInStock())
                .collect(Collectors.toList());
    }

    private Voucher validateVoucher(String code) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_VOUCHER));

        LocalDateTime now = LocalDateTime.now();
        if (!voucher.getIsActive()
                || (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate()))
                || (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate()))
                || (voucher.getUsageLimit() != null && voucher.getUsedCount() >= voucher.getUsageLimit())) {
            throw new AppException(ErrorCode.INVALID_VOUCHER);
        }
        return voucher;
    }

    private BigDecimal calculateDiscount(String code, BigDecimal totalAmount) {
        Voucher voucher = validateVoucher(code);

        if (voucher.getMinOrderValue() != null && totalAmount.compareTo(voucher.getMinOrderValue()) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        switch (voucher.getDiscountType().toUpperCase()) {
            case "PERCENTAGE":
                discount = totalAmount.multiply(voucher.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                if (voucher.getMaxDiscountAmount() != null) {
                    discount = discount.min(voucher.getMaxDiscountAmount());
                }
                break;
            case "FIXED":
                discount = voucher.getDiscountValue();
                break;
            case "FREE_SHIP":
                discount = BigDecimal.ZERO; // ponytail: handled at checkout/shipping layer
                break;
            default:
                discount = BigDecimal.ZERO;
        }

        return discount;
    }

    private int getStock(Product product, ProductVariant variant) {
        if (variant != null) {
            return variant.getStockQuantity() != null ? variant.getStockQuantity() : 0;
        }
        return product.getStockQuantity() != null ? product.getStockQuantity() : 0;
    }

    private String generateItemKey(UUID productId, UUID variantId) {
        return String.join(":", productId.toString(), variantId != null ? variantId.toString() : "0");
    }

    private String getCurrentUserId() {
        return userService.getCurrentUser().getId().toString();
    }
}
