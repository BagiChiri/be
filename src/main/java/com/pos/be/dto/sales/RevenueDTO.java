package com.pos.be.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class RevenueDTO {
    private List<String> labels;
    private List<BigDecimal> data;
}