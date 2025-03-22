package com.pos.be.componet;


import com.pos.be.dto.order.OrderDTO;
import com.pos.be.dto.order.OrderItemDTO;
import com.pos.be.entity.order.Order;
import com.pos.be.entity.order.OrderItem;
import com.pos.be.entity.product.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderDTO toDTO(Order entity) {
        if (entity == null) {
            return null;
        }
        OrderDTO dto = new OrderDTO();
        dto.setId(entity.getId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setOrderDate(entity.getOrderDate());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.setOrderStatus(entity.getOrderStatus());

        if (entity.getOrderItems() != null) {
            dto.setOrderItems(
                    entity.getOrderItems().stream()
                            .map(this::toDTO)  // map each OrderItem -> OrderItemDTO
                            .collect(Collectors.toList())
            );
        }
        return dto;
    }

    public Order toEntity(OrderDTO dto) {
        if (dto == null) {
            return null;
        }
        Order entity = new Order();
        entity.setId(dto.getId());
        entity.setOrderNumber(dto.getOrderNumber());
        entity.setOrderDate(dto.getOrderDate());
        entity.setTotalPrice(dto.getTotalPrice());
        entity.setOrderStatus(dto.getOrderStatus());

        // Convert DTO items -> Entity items
        if (dto.getOrderItems() != null) {
            for (OrderItemDTO itemDTO : dto.getOrderItems()) {
                OrderItem itemEntity = toEntity(itemDTO);
                // Ensure the item references the parent Order
                itemEntity.setOrder(entity);
                entity.getOrderItems().add(itemEntity);
            }
        }
        return entity;
    }

    // OrderItem <-> OrderItemDTO

    public OrderItemDTO toDTO(OrderItem item) {
        if (item == null) {
            return null;
        }

        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getProduct_id());
        }
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }

    public OrderItem toEntity(OrderItemDTO dto) {
        if (dto == null) {
            return null;
        }
        OrderItem item = new OrderItem();
        item.setId(dto.getId());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        // The actual Product entity should be fetched if you need it,
        // e.g. via a ProductRepository. For now, we only store productId in the DTO.
        return item;
    }

    // For converting lists or pages, you can add a helper method if needed
    public List<OrderDTO> toDTOList(List<Order> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
