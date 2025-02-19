package com.pos.be.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDTO {
    private Long productId;
    private int quantity;
    private double price; //todo: use bigdecimal for price
}
