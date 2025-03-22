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

import com.pos.be.componet.OrderMapper;
import com.pos.be.dto.order.OrderDTO;
import com.pos.be.entity.order.Order;
import com.pos.be.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    // Get paginated orders, optionally searching by orderNumber
    @GetMapping("/by_name")
    public Page<OrderDTO> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String query
    ) {
        Page<Order> orderPage = orderService.getOrders(page, size, query);
        // Map each entity to a DTO
        return orderPage.map(orderMapper::toDTO);
    }

    // Fetch an order by ID
    @GetMapping("/by_id/{id}")
    public OrderDTO getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return orderMapper.toDTO(order);
    }

    // Create a new order
    @PostMapping
    public OrderDTO createOrder(@RequestBody OrderDTO orderDTO) {
        Order entityToCreate = orderMapper.toEntity(orderDTO);
        Order createdEntity = orderService.createOrder(entityToCreate);
        return orderMapper.toDTO(createdEntity);
    }

    // Update an existing order
    @PutMapping("/{id}")
    public OrderDTO updateOrder(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        Order entityToUpdate = orderMapper.toEntity(orderDTO);
        Order updatedEntity = orderService.updateOrder(id, entityToUpdate);
        return orderMapper.toDTO(updatedEntity);
    }

    // Delete an order
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
