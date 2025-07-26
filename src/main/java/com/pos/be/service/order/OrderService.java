package com.pos.be.service.order;

import com.pos.be.component.ConsignmentMapper;
import com.pos.be.dto.order.ConsignmentDTO;
import com.pos.be.dto.order.ConsignmentItemDTO;
import com.pos.be.entity.order.Consignment;
import com.pos.be.entity.order.ConsignmentItem;
import com.pos.be.entity.order.ConsignmentStatus;
import com.pos.be.entity.product.Product;
import com.pos.be.entity.transaction.Transaction;
import com.pos.be.exception.PermissionDeniedException;
import com.pos.be.repository.order.ConsignmentItemRepository;
import com.pos.be.repository.order.ConsignmentRepository;
import com.pos.be.repository.product.ProductRepository;
import com.pos.be.repository.transaction.TransactionRepository;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.security.rbac.SecurityUtils;
import com.pos.be.service.TransactionService;
import com.pos.be.specification.GenericSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ConsignmentRepository consignmentRepository;
    private final ProductRepository productRepository;
    private final ConsignmentItemRepository consignmentItemRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final ConsignmentMapper consignmentMapper;

    private static String generateOrderNumber() {
        String prefix = "ORD";

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        int randomNum = 1000 + new Random().nextInt(9000);

        return prefix + "-" + timeStamp + "-" + randomNum;
    }

    @PreAuthorize("hasAuthority('" + Permissions.READ_ORDER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Page<Consignment> getOrders(Map<String, String> filters, Pageable pageable) {
        if (!SecurityUtils.hasPermission(Permissions.READ_ORDER)) {
            throw new PermissionDeniedException("You don't have permission to view orders");
        }

        Specification<Consignment> specification = new GenericSpecification<>(filters, Consignment.class);
        return consignmentRepository.findAll(specification, pageable);
    }

    @PreAuthorize("hasAuthority('" + Permissions.READ_ORDER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Consignment getOrderById(Long id) {
        // Additional service-level permission check
        if (!SecurityUtils.hasPermission(Permissions.READ_ORDER)) {
            throw new PermissionDeniedException("You don't have permission to view orders");
        }

        return consignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @PreAuthorize("hasAuthority('" + Permissions.READ_ORDER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Consignment getOrderByOrderNumber(String orderNumber) {
        // Additional service-level permission check
        if (!SecurityUtils.hasPermission(Permissions.READ_ORDER)) {
            throw new PermissionDeniedException("You don't have permission to view orders");
        }

        return consignmentRepository.findByConsignmentNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with consignmentNumber: " + orderNumber));
    }

    @Transactional
    @PreAuthorize("hasAuthority('CREATE_ORDER') or hasAuthority('FULL_ACCESS')")
    public Consignment createOrder(Consignment consignment) {
        return createOrder(consignment, null);
    }

    @Transactional
    @PreAuthorize("hasAuthority('CREATE_ORDER') or hasAuthority('FULL_ACCESS')")
    public Consignment createOrder(Consignment consignment, Transaction payment) {
        // — save consignment —
        consignment.setConsignmentDate(LocalDateTime.now());
        consignment.setConsignmentStatus(
                consignment.getConsignmentStatus() == null
                        ? ConsignmentStatus.PENDING
                        : consignment.getConsignmentStatus()
        );
        consignment.setConsignmentNumber(generateOrderNumber());
        consignment = consignmentRepository.save(consignment);

        double total = 0.0;
        List<ConsignmentItem> savedItems = new ArrayList<>();
        for (ConsignmentItem item : consignment.getConsignmentItems()) {
            var product = productRepository.findById(item.getProduct().getProduct_id())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            if (product.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock");
            }
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            item.setConsignment(consignment);
            total += item.getPrice() * item.getQuantity();
            savedItems.add(consignmentItemRepository.save(item));
        }
        consignment.setConsignmentItems(savedItems);
        consignment.setTotalPrice(total);
        consignmentRepository.save(consignment);

        if (payment != null) {
            payment.setConsignment(consignment);
            payment.setTotalAmount(BigDecimal.valueOf(total));
            transactionService.createTransaction(payment);
        }

        return consignment;
    }

    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_ORDER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    @Transactional
    public ResponseEntity<?> updateOrder(Long id, ConsignmentDTO dto) {
        if (!SecurityUtils.hasPermission(Permissions.UPDATE_ORDER)) {
            throw new PermissionDeniedException("You don't have permission to update orders");
        }

        Consignment consignment = consignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));

        if (dto.getOrderItems() == null || dto.getOrderItems().isEmpty()) {
            for (ConsignmentItem ci : consignment.getConsignmentItems()) {
                Long productId = ci.getProduct().getProduct_id();
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
                product.setQuantity(product.getQuantity() + ci.getQuantity());
                productRepository.save(product);
            }
            consignmentRepository.delete(consignment);
            return ResponseEntity.noContent().build();
        }

        consignment.setConsignmentNumber(dto.getConsignmentNumber());
        consignment.setConsignmentStatus(dto.getConsignmentStatus());
        consignment.setConsignmentDate(dto.getOrderDate());
        consignment.setCustomerName(dto.getCustomerName());

        Map<Long, ConsignmentItem> existingByItemId = consignment.getConsignmentItems().stream()
                .collect(Collectors.toMap(ConsignmentItem::getId, Function.identity()));

        Map<Long, Integer> oldQtyByProduct = consignment.getConsignmentItems().stream()
                .collect(Collectors.toMap(
                        ci -> ci.getProduct().getProduct_id(),
                        ConsignmentItem::getQuantity
                ));

        List<ConsignmentItem> mergedItems = new ArrayList<>();
        for (ConsignmentItemDTO itemDto : dto.getOrderItems()) {
            Long itemId = itemDto.getId();
            Long productId = itemDto.getProductId();
            int newQty = itemDto.getQuantity();
            int oldQty = oldQtyByProduct.getOrDefault(productId, 0);
            int delta = newQty - oldQty;

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
            if (delta > 0) {
                if (product.getQuantity() < delta) {
                    throw new RuntimeException("Not enough stock for product " + productId);
                }
                product.setQuantity(product.getQuantity() - delta);
            } else if (delta < 0) {
                product.setQuantity(product.getQuantity() + (-delta));
            }
            productRepository.save(product);

            if (itemId != null) {
                ConsignmentItem existing = existingByItemId.get(itemId);
                if (existing == null) {
                    throw new EntityNotFoundException("Order item not found: " + itemId);
                }
                existing.setQuantity(newQty);
                existing.setPrice(itemDto.getPrice());
                mergedItems.add(existing);
            } else {
                ConsignmentItem created = new ConsignmentItem();
                created.setProduct(product);
                created.setQuantity(newQty);
                created.setPrice(itemDto.getPrice());
                created.setConsignment(consignment);
                mergedItems.add(created);
            }
        }

        consignment.getConsignmentItems().clear();
        consignment.getConsignmentItems().addAll(mergedItems);

        double total = mergedItems.stream()
                .mapToDouble(ci -> ci.getPrice() * ci.getQuantity())
                .sum();
        consignment.setTotalPrice(total);

        Consignment saved = consignmentRepository.save(consignment);
        return ResponseEntity.ok(consignmentMapper.toDTO(saved));
    }

    @Transactional
    @PreAuthorize("hasAuthority('" + Permissions.DELETE_ORDER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public void deleteOrder(Long id) {
        if (!SecurityUtils.hasPermission(Permissions.DELETE_ORDER)) {
            throw new PermissionDeniedException("You don't have permission to delete orders");
        }

        Consignment consignment = getOrderById(id);

        consignmentItemRepository.deleteByConsignmentId(id);
        transactionRepository.deleteByConsignmentId(id);

        consignmentRepository.delete(consignment);
    }

    @PreAuthorize("hasAuthority('" + Permissions.READ_ORDER + "') or hasAnyAuthority('" + Permissions.FULL_ACCESS + "')")
    public Map<String, Long> getOrderStatusSummary() {
        if (!SecurityUtils.hasPermission(Permissions.READ_ORDER)) {
            throw new PermissionDeniedException("You don't have permission to view orders");
        }
        try {
            Map<ConsignmentStatus, Long> orderStatusSummary = new EnumMap<>(ConsignmentStatus.class);
            for (ConsignmentStatus status : ConsignmentStatus.values()) {
                long count = consignmentRepository.countByConsignmentStatus(status);
                orderStatusSummary.put(status, count);
            }

            Map<String, Long> result = new HashMap<>();
            for (Map.Entry<ConsignmentStatus, Long> entry : orderStatusSummary.entrySet()) {
                result.put(entry.getKey().name(), entry.getValue());
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get order status summary", e);
        }
    }

    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_ORDER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public ResponseEntity<?> updateConsignmentStatus(String consignmentNumber, ConsignmentStatus status) {

        if (!SecurityUtils.hasPermission(Permissions.UPDATE_ORDER)) {
            throw new PermissionDeniedException("You don't have permission to view orders");
        }


        return consignmentRepository.findByConsignmentNumber(consignmentNumber)
                .map(consignment -> {
                    consignment.setConsignmentStatus(status);
                    consignmentRepository.save(consignment);
                    return ResponseEntity.ok("Consignment status updated successfully.");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Consignment with number: " + consignmentNumber + " not found."));
    }
}