package com.pos.be.dto.sales;

import com.pos.be.constants.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentMethodStatsDTO {
    private PaymentMethod method;
    private Long count;
}