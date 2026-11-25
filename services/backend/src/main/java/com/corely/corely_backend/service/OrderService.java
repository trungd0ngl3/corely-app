package com.corely.corely_backend.service;

import com.corely.corely_backend.dto.request.order.OrderCreationRequest;
import com.corely.corely_backend.dto.response.cart.CartItemResponse;
import com.corely.corely_backend.dto.response.cart.CartResponse;
import com.corely.corely_backend.dto.response.order.OrderResponse;
import com.corely.corely_backend.entity.*;
import com.corely.corely_backend.enums.OrderStatus;
import com.corely.corely_backend.exception.AppException;
import com.corely.corely_backend.exception.ErrorCode;
import com.corely.corely_backend.factory.OrderFactory;
import com.corely.corely_backend.mapper.OrderMapper;
import com.corely.corely_backend.repository.OrderRepository;
import com.corely.corely_backend.repository.ProductRepository;
import com.corely.corely_backend.repository.ProductVariantRepository;
import com.corely.corely_backend.repository.StoreRepository;
import com.corely.corely_backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.corely.corely_backend.factory.OrderItemFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    OrderRepository orderRepository;
    StoreRepository storeRepository;
    ProductRepository productRepository;
    ProductVariantRepository productVariantRepository;

    CartService cartService;
    OrderTimelineService orderTimelineService;
    OrderValidationService orderValidationService;
    OrderPermissionService orderPermissionService;
    OrderCalculatorService orderCalculatorService;
    InventoryService inventoryService;

    OrderFactory orderFactory;
    OrderItemFactory orderItemFactory;

    OrderMapper orderMapper;

    SecurityUtils securityUtils;

    @Transactional
    public OrderResponse createOrder(OrderCreationRequest request) {
        User user = securityUtils.getCurrentUser();

        Store store = storeRepository.findTopBy()
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));

        CartResponse cart = cartService.getCart();
        orderValidationService.validateCart(cart);

        List<CartItemResponse> storeItems = cart.getItems();
        Order order = orderFactory.create(request, user, store);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItemResponse itemResponse : storeItems) {
            Product product = productRepository.findById(itemResponse.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            ProductVariant variant = null;
            if (itemResponse.getVariantId() != null) {
                variant = productVariantRepository.findById(itemResponse.getVariantId())
                        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            }

            orderValidationService.validateProduct(product);
            orderValidationService.validateVariant(variant);

            int availableStock = (variant != null) ? variant.getStockQuantity() : product.getStockQuantity();
            orderValidationService.validateStock(availableStock, itemResponse.getQuantity());

            OrderItem orderItem = orderItemFactory.create(product, variant, itemResponse.getQuantity(), order);
            orderItems.add(orderItem);
        }

        BigDecimal subtotal = orderCalculatorService.calculateSubtotal(orderItems);
        BigDecimal shippingFee = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal totalAmount = orderCalculatorService.calculateTotal(subtotal, shippingFee, discount);

        orderFactory.finalizeOrder(order, orderItems, subtotal, shippingFee, discount, totalAmount);

        inventoryService.deduct(orderItems);

        order = orderRepository.save(order);

        orderTimelineService.recordStatusChange(order, null, order.getStatus(), "Order created");

        TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                for (CartItemResponse item : storeItems) {
                    cartService.removeFromCart(
                            item.getProductId(),
                            item.getVariantId());
                }
            }
        });

        return orderMapper.toOrderResponse(order);
    }

    public Page<OrderResponse> getMyOrder(Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(securityUtils.getCurrentUserId(), pageable)
                .map(orderMapper::toOrderResponse);
    }

    public Page<OrderResponse> getStoreOrder(Pageable pageable) {
        Store store = storeRepository.findTopBy()
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND));

        return orderRepository.findByStoreIdOrderByCreatedAtDesc(store.getId(), pageable)
                .map(orderMapper::toOrderResponse);
    }

    public OrderResponse getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        User currentUser = securityUtils.getCurrentUser();

        boolean isOwner = order.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles()
                .stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        User currentUser = securityUtils.getCurrentUser();
        
        boolean isAdmin = currentUser.getRoles()
                .stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));

        boolean isOwner = order.getUser().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        OrderStatus oldStatus = order.getStatus();
        orderPermissionService.validateTransition(order, status, isAdmin);

        order.setStatus(status);
        order = orderRepository.save(order);

        orderTimelineService.recordStatusChange(order, oldStatus, status, "Status updated");

        return orderMapper.toOrderResponse(order);
    }
}
