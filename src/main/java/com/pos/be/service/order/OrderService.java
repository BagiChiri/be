package com.pos.be.service.order;

import com.pos.be.entity.order.Consignment;
import com.pos.be.entity.order.ConsignmentItem;
import com.pos.be.exception.PermissionDeniedException;
import com.pos.be.repository.order.ConsignmentItemRepository;
import com.pos.be.repository.order.ConsignmentRepository;
import com.pos.be.repository.product.ProductRepository;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.security.rbac.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ConsignmentRepository consignmentRepository;
    private final ProductRepository productRepository;
    private final ConsignmentItemRepository consignmentItemRepository;

    @PreAuthorize("hasAuthority('" + Permissions.ORDER_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Page<Consignment> getOrders(int page, int size, String searchTerm) {
        // Additional service-level permission check
        if (!SecurityUtils.hasPermission(Permissions.ORDER_VIEW)) {
            throw new PermissionDeniedException("You don't have permission to view orders");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return consignmentRepository.findAll(
                    (root, query, cb) -> cb.like(root.get("orderNumber"), "%" + searchTerm + "%"),
                    pageable
            );
        }
        return consignmentRepository.findAll(pageable);
    }

    @PreAuthorize("hasAuthority('" + Permissions.ORDER_VIEW + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Consignment getOrderById(Long id) {
        // Additional service-level permission check
        if (!SecurityUtils.hasPermission(Permissions.ORDER_VIEW)) {
            throw new PermissionDeniedException("You don't have permission to view orders");
        }

        return consignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Permissions.ORDER_CREATE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Consignment createOrder(Consignment consignment) {
        // Additional service-level permission check
        if (!SecurityUtils.hasPermission(Permissions.ORDER_CREATE)) {
            throw new PermissionDeniedException("You don't have permission to create orders");
        }

        consignment.setConsignmentDate(LocalDateTime.now());
        consignment = consignmentRepository.save(consignment);

        double totalPrice = 0.0;
        List<ConsignmentItem> items = new ArrayList<>();

        if (consignment.getConsignmentItems() != null) {
            for (ConsignmentItem item : consignment.getConsignmentItems()) {
                var product = productRepository.findById(item.getProduct().getProduct_id())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                if (product.getQuantity() < item.getQuantity()) {
                    throw new RuntimeException("Not enough stock for product: " + product.getProduct_id());
                }

                product.setQuantity(product.getQuantity() - item.getQuantity());
                productRepository.save(product);

                item.setConsignment(consignment);
                totalPrice += item.getPrice() * item.getQuantity();
                items.add(consignmentItemRepository.save(item));
            }
        }

        consignment.setConsignmentItems(items);
        consignment.setTotalPrice(totalPrice);
        consignmentItemRepository.saveAll(items);
        return consignmentRepository.save(consignment);
    }

    @PreAuthorize("hasAuthority('" + Permissions.ORDER_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Consignment updateOrder(Long id, Consignment updatedConsignment) {
        // Additional service-level permission check
        if (!SecurityUtils.hasPermission(Permissions.ORDER_MANAGE)) {
            throw new PermissionDeniedException("You don't have permission to update orders");
        }

        Consignment existing = getOrderById(id);
        existing.setConsignmentNumber(updatedConsignment.getConsignmentNumber());
        existing.setOrderStatus(updatedConsignment.getOrderStatus());
        existing.setConsignmentDate(updatedConsignment.getConsignmentDate());
        existing.getConsignmentItems().clear();

        double totalPrice = 0.0;
        if (updatedConsignment.getConsignmentItems() != null) {
            for (ConsignmentItem item : updatedConsignment.getConsignmentItems()) {
                item.setConsignment(existing);
                totalPrice += item.getPrice() * item.getQuantity();
                existing.getConsignmentItems().add(item);
            }
        }
        existing.setTotalPrice(totalPrice);
        return consignmentRepository.save(existing);
    }

    @PreAuthorize("hasAuthority('" + Permissions.ORDER_MANAGE + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public void deleteOrder(Long id) {
        // Additional service-level permission check
        if (!SecurityUtils.hasPermission(Permissions.ORDER_MANAGE)) {
            throw new PermissionDeniedException("You don't have permission to delete orders");
        }

        Consignment consignment = getOrderById(id);
        consignmentRepository.delete(consignment);
    }
}