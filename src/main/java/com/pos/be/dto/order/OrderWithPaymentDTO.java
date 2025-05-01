package com.pos.be.dto.order;

// com/pos/be/dto/OrderWithPaymentDTO.java
import com.pos.be.dto.transactions.TransactionDTO;
import lombok.Data;

@Data
public class OrderWithPaymentDTO {
    private ConsignmentDTO order;
    private TransactionDTO payment;
}
