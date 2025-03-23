package com.pos.be.service.order;

import com.pos.be.entity.order.Consignment;
import com.pos.be.entity.order.ConsignmentItem;
import com.pos.be.repository.order.ConsignmentItemRepository;
import com.pos.be.repository.order.ConsignmentRepository;
import com.pos.be.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
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


    public Page<Consignment> getOrders(int page, int size, String searchTerm) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        if (searchTerm != null && !searchTerm.isEmpty()) {
            // Filter by orderNumber (or another property if needed)
            return consignmentRepository.findAll(
                    (root, query, cb) -> cb.like(root.get("orderNumber"), "%" + searchTerm + "%"),
                    pageable
            );
        } else {
            return consignmentRepository.findAll(pageable);
        }
    }

    public Consignment getOrderById(Long id) {
        return consignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Transactional
    public Consignment createOrder(Consignment consignment) {
        // Set the order date
        consignment.setConsignmentDate(LocalDateTime.now());

        // Save the order first to generate its ID
        consignment = consignmentRepository.save(consignment);

        // Calculate total price and save order items
        double totalPrice = 0.0;
        List<ConsignmentItem> items = new ArrayList<>();

        if (consignment.getConsignmentItems() != null) {
            for (ConsignmentItem item : consignment.getConsignmentItems()) {
                // Fetch the product
                var product = productRepository.findById(item.getProduct().getProduct_id())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                // Check stock
                if (product.getQuantity() < item.getQuantity()) {
                    throw new RuntimeException("Not enough stock for product: " + product.getProduct_id());
                }

                // Update stock
                product.setQuantity(product.getQuantity() - item.getQuantity());
                productRepository.save(product);

                // Ensure the item references the saved order
                item.setConsignment(consignment);
                totalPrice += item.getPrice() * item.getQuantity();

                // Save the order item
                items.add(consignmentItemRepository.save(item));
            }
        }

        // Set the total price and order items
        consignment.setConsignmentItems(items);
        consignment.setTotalPrice(totalPrice);
        consignmentItemRepository.saveAll(items);
        // Save the order again to update the total price
        return consignmentRepository.save(consignment);
    }



    public Consignment updateOrder(Long id, Consignment updatedConsignment) {
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

    public void deleteOrder(Long id) {
        Consignment consignment = getOrderById(id);
        consignmentRepository.delete(consignment);
    }
}
