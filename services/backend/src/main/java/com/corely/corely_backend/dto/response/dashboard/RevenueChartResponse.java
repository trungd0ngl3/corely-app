package com.corely.corely_backend.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueChartResponse {
    private LocalDate date;
    private BigDecimal revenue;
    private Long orderCount;
    private BigDecimal averageOrderValue;
}
