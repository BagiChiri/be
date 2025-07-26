package com.pos.be.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsignmentItemDTO {
    private Long id;
    private Long productId;
    private int quantity;
    private double price;
}
