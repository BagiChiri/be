package com.pos.be.entity.order;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pos.be.entity.transaction.Transaction;
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
    private String customerName;

    @Enumerated(EnumType.STRING)
    private ConsignmentStatus consignmentStatus;

    @OneToMany(mappedBy = "consignment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Prevents recursion during serialization
    private List<ConsignmentItem> consignmentItems = new ArrayList<>();

    @OneToOne(mappedBy = "consignment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Transaction transaction;

    // Helper method to add items
    public void addConsignmentItem(ConsignmentItem item) {
        consignmentItems.add(item);
        item.setConsignment(this);
    }

    public void setTransaction(Transaction tx) {
        this.transaction = tx;
        if (tx != null) {
            tx.setConsignment(this);
        }
    }
}
