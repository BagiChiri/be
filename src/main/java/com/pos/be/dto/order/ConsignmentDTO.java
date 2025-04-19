//package com.pos.be.dto.order;
//
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Getter
//@Setter
//public class OrderDTO {
//    private Long id;
//    private LocalDateTime orderDate;
//    private double totalAmount;
//    private String status;
//    private List<OrderItemDTO> items;
//}
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
    private String orderNumber;
    private String customerName;
    private LocalDateTime orderDate;
    private Double totalPrice;     // Matches "totalPrice" in your frontend
    private ConsignmentStatus consignmentStatus;    // Matches "consignmentStatus" in your frontend
    private List<ConsignmentItemDTO> orderItems;
}
