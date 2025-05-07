package com.pos.be.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class OrdersByStatusDTO {
    private List<String> labels;
    private Map<String, List<Long>> series;
    // key = status name, value = list of counts aligned with labels
}