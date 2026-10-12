package com.corely.corely_backend.service;

import com.corely.corely_backend.dto.response.dashboard.DashboardStatsResponse;
import com.corely.corely_backend.dto.response.dashboard.RevenueChartResponse;
import com.corely.corely_backend.dto.response.order.OrderResponse;
import com.corely.corely_backend.dto.response.product.ProductResponse;
import com.corely.corely_backend.entity.Order;
import com.corely.corely_backend.enums.OrderStatus;
import com.corely.corely_backend.mapper.OrderMapper;
import com.corely.corely_backend.mapper.ProductMapper;
import com.corely.corely_backend.repository.OrderRepository;
import com.corely.corely_backend.repository.ProductRepository;
import com.corely.corely_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
        private final OrderRepository orderRepository;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;
        private final OrderMapper orderMapper;
        private final ProductMapper productMapper;

        public DashboardStatsResponse getDashboardStats() {
                log.debug("Calculating dashboard stats");

                BigDecimal totalRevenue = orderRepository.sumRevenueDelivered();
                long totalOrder = orderRepository.count();
                long totalProducts = productRepository.count();
                long totalCustomers = userRepository.count();
                long pendingOrder = orderRepository.countByStatus(OrderStatus.AWAITING_CONFIRMATION)
                                + orderRepository.countByStatus(OrderStatus.PENDING_PAYMENT);
                long completedOrder = orderRepository.countByStatus(OrderStatus.DELIVERED);

                BigDecimal averageOrderValue = completedOrder > 0
                                ? totalRevenue.divide(BigDecimal.valueOf(completedOrder), 2, java.math.RoundingMode.HALF_UP)
                                : BigDecimal.ZERO;

                return DashboardStatsResponse.builder()
                                .totalRevenue(totalRevenue)
                                .totalOrder(totalOrder)
                                .totalProducts(totalProducts)
                                .totalCustomers(totalCustomers)
                                .averageOrderValue(averageOrderValue)
                                .orderCompletionRate(totalOrder > 0 ? (completedOrder * 100.0) / totalOrder : 0.0)
                                .pendingOrder(pendingOrder)
                                .completedOrder(completedOrder)
                                .build();
        }

        public List<RevenueChartResponse> getRevenueChart(int days) {
                log.info("Getting revenue chart for {} days", days);

                LocalDate startDate = LocalDate.now().minusDays(days);
                LocalDateTime startDateTime = startDate.atStartOfDay();

                List<Order> Order = orderRepository.findAll().stream()
                                .filter(order -> order.getCreatedAt().isAfter(startDateTime))
                                .collect(Collectors.toList());

                return Order.stream()
                                .collect(Collectors.groupingBy(order -> order.getCreatedAt().toLocalDate()))
                                .entrySet()
                                .stream()
                                .map(entry -> {
                                        LocalDate date = entry.getKey();
                                        List<Order> dayOrder = entry.getValue();
                                        BigDecimal dayRevenue = dayOrder.stream()
                                                        .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                                                        .map(order -> order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        Long orderCount = (long) dayOrder.size();
                                        BigDecimal avgValue = orderCount > 0
                                                        ? dayRevenue.divide(BigDecimal.valueOf(orderCount))
                                                        : BigDecimal.ZERO;

                                        return RevenueChartResponse.builder()
                                                        .date(date)
                                                        .revenue(dayRevenue)
                                                        .orderCount(orderCount)
                                                        .averageOrderValue(avgValue)
                                                        .build();
                                })
                                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                                .collect(Collectors.toList());
        }

        public List<ProductResponse> getTopProducts(int limit) {
                log.info("Getting top {} products", limit);

                return productRepository.findAll().stream()
                                .limit(limit)
                                .map(productMapper::toProductResponse)
                                .collect(Collectors.toList());
        }

        public List<OrderResponse> getRecentOrder(int limit) {
                log.debug("Getting recent {} Order", limit);

                return orderRepository.findTop10ByOrderByCreatedAtDesc().stream()
                                .limit(limit)
                                .map(orderMapper::toOrderResponse)
                                .collect(Collectors.toList());
        }
}
