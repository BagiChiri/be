package com.pos.be.entity.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "custom_options")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class CustomOptions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_options_id")
    @JsonIgnore
    private Long id;

    private String optionLabel;
    private String optionType;
    @ElementCollection
    @CollectionTable(name = "custom_options_values", joinColumns = @JoinColumn(name = "custom_options_id"))
    @Column(name = "value")
    private List<String> optionValue = new ArrayList<>();

    @ManyToOne
    @JoinColumn(
            name = "product_id",
            nullable = false,
            referencedColumnName = "product_id",
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE")
    )
    @JsonIgnore
    private Product product;//*

    public CustomOptions(String optionLabel, String optionType, List<String> optionValue, Product product) {
        this.optionLabel = optionLabel;
        this.optionType = optionType;
        this.optionValue = optionValue;
        this.product = product;
    }
}
