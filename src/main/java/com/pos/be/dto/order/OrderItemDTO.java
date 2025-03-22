//package com.pos.be.dto.order;
//
//import lombok.Getter;
//import lombok.Setter;
//
//@Getter
//@Setter
//public class OrderItemDTO {
//    private Long productId;
//    private int quantity;
//    private double price; //todo: use bigdecimal for price
//}
package com.pos.be.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDTO {
    private Long id;
    private Long productId; // We'll store the Product's ID here
    private int quantity;
    private double price;
}
