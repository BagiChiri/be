package com.pos.be.entity.order;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long consignmentId;

    private String consignmentNumber;

    private LocalDateTime consignmentDate;

    private Double totalPrice;

    private String orderStatus;

    @OneToMany(mappedBy = "consignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConsignmentItem> consignmentItems = new ArrayList<>();



    // Helper method to add items
    public void addConsignmentItem(ConsignmentItem item) {
        consignmentItems.add(item);
        item.setConsignment(this);
    }
}
