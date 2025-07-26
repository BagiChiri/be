package com.pos.be.entity.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pos.be.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsignmentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Reference to parent Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consignment_id")
    @JsonBackReference
    private Consignment consignment;

    // Reference to the purchased Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    private Double price;
}
