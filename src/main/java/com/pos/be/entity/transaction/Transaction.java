package com.pos.be.entity.transaction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pos.be.constants.PaymentMethod;
import com.pos.be.constants.TransactionStatus;
import com.pos.be.entity.order.Consignment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "transactions")
@SQLDelete(sql = "UPDATE transactions SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private BigDecimal changeAmount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    @OneToOne
    @JoinColumn(name = "consignment_id", nullable = false)
    @JsonBackReference
    private Consignment consignment;
    private String processedByUsername;

    private LocalDateTime transactionDate;

    private String referenceNumber;

    private String remarks;
    private boolean deleted = false;
    private String cardTrackData;
}
