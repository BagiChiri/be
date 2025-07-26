package com.pos.be.dto.order;

import com.pos.be.dto.transactions.TransactionDTO;
import lombok.Data;

@Data
public class OrderWithPaymentDTO {
    private ConsignmentDTO order;
    private TransactionDTO payment;
}
