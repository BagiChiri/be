package com.pos.be.entity.shipper;

import com.pos.be.entity.order.Orders;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Shipper {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long shipperId;
    private String shipperName;
    private String phoneNumber;

    @OneToMany(mappedBy = "shipper", cascade = CascadeType.ALL)
    private List<Orders> orders = new ArrayList<>();
}
