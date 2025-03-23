//package com.pos.be.controller.order;
//
//import com.pos.be.entity.order.Order;
//import com.pos.be.service.order.OrderService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/order")
//@CrossOrigin // Adjust the CORS settings as needed
//public class OrderController {
//
//    private final OrderService orderService;
//
//    @Autowired
//    public OrderController(OrderService orderService) {
//        this.orderService = orderService;
//    }
//
//    // Endpoint to get paginated orders, with optional search by order number
//    @GetMapping("/by_name")
//    public Page<Order> getOrders(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "12") int size,
//            @RequestParam(required = false) String query
//    ) {
//        return orderService.getOrders(page, size, query);
//    }
//
//    // Endpoint to fetch an order by its ID
//    @GetMapping("/by_id/{id}")
//    public Order getOrderById(@PathVariable Long id) {
//        return orderService.getOrderById(id);
//    }
//
//    // Create a new order
//    @PostMapping
//    public Order createOrder(@RequestBody Order order) {
//        return orderService.createOrder(order);
//    }
//
//    // Update an existing order
//    @PutMapping("/{id}")
//    public Order updateOrder(@PathVariable Long id, @RequestBody Order order) {
//        return orderService.updateOrder(id, order);
//    }
//
//    // Delete an order
//    @DeleteMapping("/{id}")
//    public void deleteOrder(@PathVariable Long id) {
//        orderService.deleteOrder(id);
//    }
//}
package com.pos.be.controller.order;

import com.pos.be.component.ConsignmentMapper;
import com.pos.be.dto.order.ConsignmentDTO;
import com.pos.be.entity.order.Consignment;
import com.pos.be.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;
    private final ConsignmentMapper consignmentMapper;

    @Autowired
    public OrderController(OrderService orderService, ConsignmentMapper consignmentMapper) {
        this.orderService = orderService;
        this.consignmentMapper = consignmentMapper;
    }

    // Get paginated orders, optionally searching by orderNumber
    @GetMapping("/by_name")
    public Page<ConsignmentDTO> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String query
    ) {
        Page<Consignment> orderPage = orderService.getOrders(page, size, query);
        // Map each entity to a DTO
        return orderPage.map(consignmentMapper::toDTO);
    }

    // Fetch an order by ID
    @GetMapping("/by_id/{id}")
    public ConsignmentDTO getOrderById(@PathVariable Long id) {
        Consignment consignment = orderService.getOrderById(id);
        return consignmentMapper.toDTO(consignment);
    }

    // Create a new order
    @PostMapping
    public ConsignmentDTO createOrder(@RequestBody ConsignmentDTO consignmentDTO) {
        Consignment entityToCreate = consignmentMapper.toEntity(consignmentDTO);
        Consignment createdEntity = orderService.createOrder(entityToCreate);
        return consignmentMapper.toDTO(createdEntity);
    }

    // Update an existing order
    @PutMapping("/{id}")
    public ConsignmentDTO updateOrder(@PathVariable Long id, @RequestBody ConsignmentDTO consignmentDTO) {
        Consignment entityToUpdate = consignmentMapper.toEntity(consignmentDTO);
        Consignment updatedEntity = orderService.updateOrder(id, entityToUpdate);
        return consignmentMapper.toDTO(updatedEntity);
    }

    // Delete an order
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
