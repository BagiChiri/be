package com.pos.be.entity.customer;

import com.pos.be.entity.order.Orders;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long customerId;
    private String name;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private String phoneNumber;

    @OneToMany(mappedBy = "customer")
    private List<Orders> orders = new ArrayList<>();
}
