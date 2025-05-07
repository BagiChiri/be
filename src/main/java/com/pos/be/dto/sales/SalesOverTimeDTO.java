package com.pos.be.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SalesOverTimeDTO {
    private LocalDate date;
    private BigDecimal totalSales;
}