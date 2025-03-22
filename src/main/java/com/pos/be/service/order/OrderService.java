package com.pos.be.service.order;

import com.pos.be.entity.order.Order;
import com.pos.be.entity.order.OrderItem;
import com.pos.be.repository.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Page<Order> getOrders(int page, int size, String searchTerm) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        if (searchTerm != null && !searchTerm.isEmpty()) {
            // Filter by orderNumber (or another property if needed)
            return orderRepository.findAll(
                    (root, query, cb) -> cb.like(root.get("orderNumber"), "%" + searchTerm + "%"),
                    pageable
            );
        } else {
            return orderRepository.findAll(pageable);
        }
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public Order createOrder(Order order) {
        order.setOrderDate(LocalDateTime.now());
        double totalPrice = 0.0;
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                item.setOrder(order);
                totalPrice += item.getPrice() * item.getQuantity();
            }
        }
        order.setTotalPrice(totalPrice);
        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, Order updatedOrder) {
        Order existing = getOrderById(id);
        existing.setOrderNumber(updatedOrder.getOrderNumber());
        existing.setOrderStatus(updatedOrder.getOrderStatus());
        existing.setOrderDate(updatedOrder.getOrderDate());
        existing.getOrderItems().clear();
        double totalPrice = 0.0;
        if (updatedOrder.getOrderItems() != null) {
            for (OrderItem item : updatedOrder.getOrderItems()) {
                item.setOrder(existing);
                totalPrice += item.getPrice() * item.getQuantity();
                existing.getOrderItems().add(item);
            }
        }
        existing.setTotalPrice(totalPrice);
        return orderRepository.save(existing);
    }

    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }
}
