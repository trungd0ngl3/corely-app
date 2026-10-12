package com.corely.corely_backend.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private BigDecimal totalRevenue;
    private Long totalOrder;
    private Long totalProducts;
    private Long totalCustomers;
    private BigDecimal averageOrderValue;
    private Double orderCompletionRate;
    private Long pendingOrder;
    private Long completedOrder;
}
