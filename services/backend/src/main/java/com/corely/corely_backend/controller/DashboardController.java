package com.corely.corely_backend.controller;

import com.corely.corely_backend.dto.response.dashboard.DashboardStatsResponse;
import com.corely.corely_backend.dto.response.dashboard.RevenueChartResponse;
import com.corely.corely_backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.corely.corely_backend.dto.response.ApiResponse;
import com.corely.corely_backend.dto.response.order.OrderResponse;
import com.corely.corely_backend.dto.response.product.ProductResponse;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DashboardStatsResponse> getDashboardSummary() {
        log.debug("Getting dashboard summary");
        return ApiResponse.<DashboardStatsResponse>builder()
                .result(dashboardService.getDashboardStats())
                .build();
    }

    @GetMapping("/revenues")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<RevenueChartResponse>> getRevenues(
            @RequestParam(defaultValue = "7") int days) {
        log.debug("Getting revenues for last {} days", days);
        return ApiResponse.<List<RevenueChartResponse>>builder()
                .result(dashboardService.getRevenueChart(days))
                .build();
    }

    @GetMapping("/products/top")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ProductResponse>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit) {
        log.debug("Getting top {} products", limit);
        return ApiResponse.<List<ProductResponse>>builder()
                .result(dashboardService.getTopProducts(limit))
                .build();
    }

    @GetMapping("/Order")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<OrderResponse>> getRecentOrder(
            @RequestParam(defaultValue = "10") int limit) {
        log.debug("Getting recent {} Order", limit);
        return ApiResponse.<List<OrderResponse>>builder()
                .result(dashboardService.getRecentOrder(limit))
                .build();
    }
}
