package com.pos.be.component;

import com.pos.be.dto.order.ConsignmentDTO;
import com.pos.be.dto.order.ConsignmentItemDTO;
import com.pos.be.entity.order.Consignment;
import com.pos.be.entity.order.ConsignmentItem;
import com.pos.be.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConsignmentMapper {

    private final ProductRepository productRepository;

    @Transactional
    public ConsignmentDTO toDTO(Consignment entity) {
        if (entity == null) {
            return null;
        }
        ConsignmentDTO dto = new ConsignmentDTO();
        dto.setId(entity.getConsignmentId());
        dto.setConsignmentNumber(entity.getConsignmentNumber());
        dto.setOrderDate(entity.getConsignmentDate());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.setCustomerName(entity.getCustomerName());
        dto.setConsignmentStatus(entity.getConsignmentStatus());

        if (entity.getConsignmentItems() != null) {
            dto.setOrderItems(
                    entity.getConsignmentItems().stream()
                            .map(this::toDTO)
                            .collect(Collectors.toList())
            );
        }
        return dto;
    }

    public Consignment toEntity(ConsignmentDTO dto) {
        if (dto == null) {
            return null;
        }
        Consignment entity = new Consignment();
        entity.setConsignmentId(dto.getId());
        entity.setConsignmentNumber(dto.getConsignmentNumber());
        entity.setConsignmentDate(dto.getOrderDate());
        entity.setTotalPrice(dto.getTotalPrice());
        entity.setCustomerName(dto.getCustomerName());
        entity.setConsignmentStatus(dto.getConsignmentStatus());

        List<ConsignmentItem> consignmentItems = new ArrayList<>();
        if (dto.getOrderItems() != null) {
            for (ConsignmentItemDTO itemDTO : dto.getOrderItems()) {
                ConsignmentItem itemEntity = toEntity(itemDTO);
                itemEntity.setConsignment(entity);
                consignmentItems.add(itemEntity);
            }
        }
        entity.setConsignmentItems(consignmentItems);
        return entity;
    }

    public ConsignmentItemDTO toDTO(ConsignmentItem item) {
        if (item == null) {
            return null;
        }

        ConsignmentItemDTO dto = new ConsignmentItemDTO();
        dto.setId(item.getId());
        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getProduct_id());
        }
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }

    public ConsignmentItem toEntity(ConsignmentItemDTO dto) {
        if (dto == null) {
            return null;
        }
        ConsignmentItem item = new ConsignmentItem();
        productRepository.findById(dto.getProductId()).ifPresent(item::setProduct);
        item.setId(dto.getId());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        return item;
    }

    public List<ConsignmentDTO> toDTOList(List<Consignment> entities) {
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
