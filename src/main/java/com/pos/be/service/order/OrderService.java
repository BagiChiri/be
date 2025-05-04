package com.pos.be.service.order;

import com.pos.be.component.ConsignmentMapper;
import com.pos.be.dto.order.ConsignmentDTO;
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

        // Format: YYYYMMDDHHMMSS
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // Generate a 4-digit random number
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

//    @Transactional
//    @PreAuthorize("hasAuthority('" + Permissions.CREATE_ORDER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public Consignment createOrder(Consignment consignment) {
//        // Additional service-level permission check
//        if (!SecurityUtils.hasPermission(Permissions.CREATE_ORDER)) {
//            throw new PermissionDeniedException("You don't have permission to create orders");
//        }
//
//        consignment.setConsignmentDate(LocalDateTime.now());
//        consignment.setConsignmentStatus(consignment.getConsignmentStatus() == null ? ConsignmentStatus.PENDING : consignment.getConsignmentStatus());
//        consignment.setConsignmentNumber(generateOrderNumber());
//        consignment = consignmentRepository.save(consignment);
//
//        double totalPrice = 0.0;
//        List<ConsignmentItem> items = new ArrayList<>();
//
//        if (consignment.getConsignmentItems() != null) {
//            for (ConsignmentItem item : consignment.getConsignmentItems()) {
//                var product = productRepository.findById(item.getProduct().getProduct_id())
//                        .orElseThrow(() -> new RuntimeException("Product not found"));
//
//                if (product.getQuantity() < item.getQuantity()) {
//                    throw new RuntimeException("Not enough stock for product: " + product.getProduct_id());
//                }
//
//                product.setQuantity(product.getQuantity() - item.getQuantity());
//                productRepository.save(product);
//
//                item.setConsignment(consignment);
//                totalPrice += item.getPrice() * item.getQuantity();
//                items.add(consignmentItemRepository.save(item));
//            }
//        }
//
//        consignment.setConsignmentItems(items);
//        consignment.setTotalPrice(totalPrice);
//        consignmentItemRepository.saveAll(items);
//        return consignmentRepository.save(consignment);
//    }

    @PreAuthorize("hasAuthority('" + Permissions.READ_ORDER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public Consignment getOrderByOrderNumber(String orderNumber) {
        // Additional service-level permission check
        if (!SecurityUtils.hasPermission(Permissions.READ_ORDER)) {
            throw new PermissionDeniedException("You don't have permission to view orders");
        }

        return consignmentRepository.findByConsignmentNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with consignmentNumber: " + orderNumber));
    }

    /**
     * Order-only flow
     **/
    @Transactional
    @PreAuthorize("hasAuthority('CREATE_ORDER') or hasAuthority('FULL_ACCESS')")
    public Consignment createOrder(Consignment consignment) {
        return createOrder(consignment, null);
    }

    /**
     * Order + immediate payment flow
     **/
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

        // — handle items & stock —
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

        // — optional immediate payment —
        if (payment != null) {
            payment.setConsignment(consignment);
            payment.setTotalAmount(BigDecimal.valueOf(total));
            transactionService.createTransaction(payment);
        }

        return consignment;
    }

    @PreAuthorize("hasAuthority('" + Permissions.UPDATE_ORDER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
//    public ResponseEntity<?> updateOrder(Long id, ConsignmentDTO updatedConsignmentDTO) {
//        // Additional service-level permission check
//        if (!SecurityUtils.hasPermission(Permissions.UPDATE_ORDER)) {
//            throw new PermissionDeniedException("You don't have permission to update orders");
//        }
//        Consignment updatedConsignment = consignmentMapper.toEntity(updatedConsignmentDTO);
//        Consignment existing = getOrderById(id);
//        existing.setConsignmentNumber(updatedConsignment.getConsignmentNumber());
//        existing.setConsignmentStatus(updatedConsignment.getConsignmentStatus());
//        existing.setConsignmentDate(updatedConsignment.getConsignmentDate());
//
//        Map<Long, Integer> existingConsignmentItems = new HashMap<>();
//        existing.getConsignmentItems().forEach(
//                (items) -> existingConsignmentItems.put(items.getConsignment().getConsignmentId(), items.getQuantity())
//        );
//
//        existing.getConsignmentItems().clear();
//
//        double totalPrice = 0.0;
//        Product product = new Product();
//
//        if (updatedConsignment.getConsignmentItems() != null) {
////            totalPrice = existing.getTotalPrice();
//            for (ConsignmentItem item : updatedConsignment.getConsignmentItems()) {
//                product = productRepository.findById(item.getProduct().getProduct_id()).orElseThrow(
//                        () -> new RuntimeException("Product not found")
//                );
//                double currentOrderItemPrice = item.getPrice();
//                item.setConsignment(existing);
//                currentOrderItemPrice += product.getPrice() * item.getQuantity();
//                totalPrice += currentOrderItemPrice;
//                existing.getConsignmentItems().add(item);
//
//                int updateQuantity = item.getQuantity() - existingConsignmentItems.get(item.getConsignment().getConsignmentId());
//                if (updateQuantity > 0) {
//                    if (product.getQuantity() < updateQuantity) {
//                        throw new RuntimeException("Insufficient stock");
//                    }
//                    product.setQuantity(product.getQuantity() - updateQuantity);
//                    productRepository.save(product);
//                } else if (updateQuantity < 0) {
//                    product.setQuantity(product.getQuantity() + (updateQuantity * (-1)));
//                    productRepository.save(product);
//                } else {
//                    consignmentItemRepository.delete(item);
//                    existing.getConsignmentItems().remove(item);
//                    product.setQuantity(product.getQuantity() + item.getQuantity());
//                    productRepository.save(product);
//                }
//            }
//        }
//        if (!existing.getConsignmentItems().isEmpty()) {
//            existing.setTotalPrice(totalPrice);
//            return new ResponseEntity<>(consignmentMapper.toDTO(consignmentRepository.save(existing)), HttpStatus.OK);
//        }
//        deleteOrder(existing.getConsignmentId());
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
    public ResponseEntity<?> updateOrder(Long id, ConsignmentDTO updatedConsignmentDTO) {
        // 1) Permission check
        if (!SecurityUtils.hasPermission(Permissions.UPDATE_ORDER)) {
            throw new PermissionDeniedException("You don't have permission to update orders");
        }

        // 2) Map incoming DTO → entity, fetch existing
        Consignment updatedConsignment = consignmentMapper.toEntity(updatedConsignmentDTO);
        Consignment existing = getOrderById(id);

        existing.setConsignmentNumber(updatedConsignment.getConsignmentNumber());
        existing.setConsignmentStatus(updatedConsignment.getConsignmentStatus());
        existing.setConsignmentDate(updatedConsignment.getConsignmentDate());

        // 3) Build a lookup of old quantities by PRODUCT ID
        Map<Long, Integer> existingQtyByProduct = new HashMap<>();
        for (ConsignmentItem ci : existing.getConsignmentItems()) {
            existingQtyByProduct.put(
                    ci.getProduct().getProduct_id(),
                    ci.getQuantity()
            );
        }

        // 4) Clear all items; we’ll re-attach nonzero ones
        existing.getConsignmentItems().clear();

        double totalPrice = 0.0;

        // 5) Process each incoming item
        if (updatedConsignment.getConsignmentItems() != null) {
            for (ConsignmentItem incomingItem : updatedConsignment.getConsignmentItems()) {
                Long pid = incomingItem.getProduct().getProduct_id();
                int newQty = incomingItem.getQuantity();
                int oldQty = existingQtyByProduct.getOrDefault(pid, 0);
                int delta  = newQty - oldQty;

                // --- ZERO-QTY BRANCH: remove completely ---
                if (newQty == 0) {
                    // restore entire oldQty back into product stock
                    Product p0 = productRepository.findById(pid)
                            .orElseThrow(() -> new RuntimeException("Product not found"));
                    p0.setQuantity(p0.getQuantity() + oldQty);
                    productRepository.save(p0);
                    // do NOT add this item to existing.getConsignmentItems()
                    continue;
                }

                // --- NONZERO QTY: adjust stock by delta ---
                Product prod = productRepository.findById(pid)
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                if (delta > 0) {
                    // user wants to reserve more stock
                    if (prod.getQuantity() < delta) {
                        throw new RuntimeException("Insufficient stock for product ID " + pid);
                    }
                    prod.setQuantity(prod.getQuantity() - delta);
                    productRepository.save(prod);
                } else if (delta < 0) {
                    // user reduced their order → return stock
                    prod.setQuantity(prod.getQuantity() + (-delta));
                    productRepository.save(prod);
                }
                // else delta == 0 → no stock change

                // attach incoming item to the consignment
                incomingItem.setConsignment(existing);
                existing.getConsignmentItems().add(incomingItem);

                // accumulate price
                totalPrice += incomingItem.getPrice() * newQty;
            }
        }

        // 6) If any items remain, save; otherwise delete the consignment entirely
        if (!existing.getConsignmentItems().isEmpty()) {
            existing.setTotalPrice(totalPrice);
            Consignment saved = consignmentRepository.save(existing);
            return ResponseEntity.ok(consignmentMapper.toDTO(saved));
        } else {
            // no items → delete the order
            deleteOrder(existing.getConsignmentId());
            return ResponseEntity.noContent().build();
        }
    }

    @PreAuthorize("hasAuthority('" + Permissions.DELETE_ORDER + "') or hasAuthority('" + Permissions.FULL_ACCESS + "')")
    public void deleteOrder(Long id) {
        // Additional service-level permission check
        if (!SecurityUtils.hasPermission(Permissions.DELETE_ORDER)) {
            throw new PermissionDeniedException("You don't have permission to delete orders");
        }

        Consignment consignment = getOrderById(id);
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