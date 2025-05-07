package com.pos.be.dto.sales;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class TransactionStatusOverTimeDTO {
    private List<String> labels;                // the date buckets
    private Map<String, List<Long>> series;     // status → list of counts aligned with labels
}