package com.pos.be.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDTO {
    private Long id;
    private LocalDateTime orderDate;
    private double totalAmount;
    private String status;
    private List<OrderItemDTO> items;
}
