package com.pos.be.entity.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pos.be.entity.category.Category;
import com.pos.be.entity.order.OrderDetail;
import com.pos.be.entity.supplier.Supplier;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class  Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    private String name;
    private String description;
    private Double price;
    private Integer quantity;

    @BatchSize(size = 250)
    @ManyToMany(
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonIgnore
    @JsonManagedReference
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(
            fetch = FetchType.LAZY
    )
    private List<Supplier> supplier = new ArrayList<>();

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<OrderDetail> orderDetails = new ArrayList<>();
}