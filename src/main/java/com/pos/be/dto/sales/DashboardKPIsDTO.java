package com.pos.be.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DashboardKPIsDTO {
    private BigDecimal totalRevenue;
    private Long totalOrders;
}