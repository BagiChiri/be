package com.pos.be.dto.transactions;

import com.pos.be.constants.PaymentMethod;
import com.pos.be.constants.TransactionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {

    private Long id;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private BigDecimal changeAmount;

    private PaymentMethod paymentMethod;

    private TransactionStatus status;

    private String processedByUsername;

    private LocalDateTime transactionDate;

    private String referenceNumber;

    private String remarks;

    private String cardTrackData;

    private Long consignmentId;
}
