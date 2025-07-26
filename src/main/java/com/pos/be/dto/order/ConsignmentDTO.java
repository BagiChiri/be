package com.pos.be.dto.order;

import com.pos.be.entity.order.ConsignmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ConsignmentDTO {
    private Long id;
    private String consignmentNumber;
    private String customerName;
    private LocalDateTime orderDate;
    private Double totalPrice;
    private ConsignmentStatus consignmentStatus;
    private List<ConsignmentItemDTO> orderItems;
    private Long transactionId;
}
