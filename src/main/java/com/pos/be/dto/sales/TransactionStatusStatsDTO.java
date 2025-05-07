package com.pos.be.dto.sales;

import com.pos.be.constants.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionStatusStatsDTO {
    private TransactionStatus status;
    private Long count;
}