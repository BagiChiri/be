package com.pos.be.dto.sales;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class PaymentMethodChartDTO {
    private List<String> labels;               // e.g. ["2025-05-01", "2025-05-02", ...]
    private Map<String, List<Long>> series;    // e.g. { "CASH": [1,0,...], "CARD": [0,2,...], ... }
}
