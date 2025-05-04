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
import com.pos.be.dto.order.OrderWithPaymentDTO;
import com.pos.be.entity.order.Consignment;
import com.pos.be.entity.transaction.Transaction;
import com.pos.be.mappers.TransactionMapper;
import com.pos.be.request.ConsignmentStatusUpdateRequest;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.service.order.OrderService;
import com.pos.be.specification.GenericSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;
    private final ConsignmentMapper consignmentMapper;
    private final TransactionMapper transactionMapper;

    @Autowired
    public OrderController(OrderService orderService, ConsignmentMapper consignmentMapper, TransactionMapper transactionMapper) {
        this.orderService = orderService;
        this.consignmentMapper = consignmentMapper;
        this.transactionMapper = transactionMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('" + Permissions.READ_ORDER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Page<Consignment> getOrders(@RequestParam Map<String, String> filters, Pageable pageable) {
        return orderService.getOrders(filters, pageable); // paging and sorting handled!
    }

    @GetMapping("/by_id/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.READ_ORDER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ConsignmentDTO getOrderById(@PathVariable Long id) {
        return consignmentMapper.toDTO(orderService.getOrderById(id));
    }

    @GetMapping("/by_consignment_number/{consignmentNumber}")
    @PreAuthorize("hasAuthority('" + Permissions.READ_ORDER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ConsignmentDTO getOrderByOrderNumber(@PathVariable String consignmentNumber) {
        return consignmentMapper.toDTO(orderService.getOrderByOrderNumber(consignmentNumber));
    }

    /** Order only **/
    @PostMapping
    public ResponseEntity<ConsignmentDTO> placeOrder(
            @RequestBody ConsignmentDTO dto
    ) {
        Consignment cons = consignmentMapper.toEntity(dto);
        Consignment saved = orderService.createOrder(cons);
        return ResponseEntity.ok(consignmentMapper.toDTO(saved));
    }

    /** Order + immediate payment **/
    @PostMapping("/with-payment")
    public ResponseEntity<ConsignmentDTO> placeOrderWithPayment(
            @RequestBody OrderWithPaymentDTO payload
    ) {
        Consignment cons   = consignmentMapper.toEntity(payload.getOrder());
        Transaction tx     = transactionMapper.toEntity(payload.getPayment());
        Consignment saved  = orderService.createOrder(cons, tx);
        return ResponseEntity.ok(consignmentMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_ORDER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody ConsignmentDTO consignmentDTO) {
        return orderService.updateOrder(id, consignmentDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.DELETE_ORDER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

    @GetMapping("/orderStatusSummary")
    @PreAuthorize("hasAuthority('" + Permissions.READ_ORDER + "') or hasAnyAuthority('" + Permissions.FULL_ACCESS + "')")
    public Map<String, Long> getConsignmentStatusSummary() {
        try {
            return orderService.getOrderStatusSummary();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to fetch order status summary", e);
        }
    }

    @PutMapping("/updateConsignmentStatus")
    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_ORDER + "') or hasAnyAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> updateConsignmentStatus(@RequestBody ConsignmentStatusUpdateRequest request) {
        return orderService.updateConsignmentStatus(request.getConsignmentNumber(), request.getStatus());
    }
}


