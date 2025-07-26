package com.pos.be.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class PaymentMethodChartDTO {
    private List<String> labels;
    private Map<String, List<Long>> series;
}
