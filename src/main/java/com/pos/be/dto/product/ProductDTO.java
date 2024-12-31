package com.pos.be.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class ProductDTO {
    @Positive(message = "Product Id Must Be Positive.")
    private Long id;

    @NotNull(message = "Product Name Must Not Be Null.")
    @NotBlank(message = "Product Name Must Not Be Blank.")
    private String name;

    @NotNull(message = "Product Description Must Not Be Null.")
    @NotBlank(message = "Product Description Must Not Be Blank.")
    private String description;

    @NotNull(message = "Product Price Required.")
    @PositiveOrZero(message = "Product Price Must Be Positive Or Zero.")
    private Double price;

    private Integer quantity;

    @NotNull(message = "Category Name Must Not Be Null.")
    @NotBlank(message = "Category Name Must Not Be Blank.")
    private Set<Long> categoryIds;

    public ProductDTO(Long id, String name, String description, Double price, Integer quantity, Set<Long> categoryIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.categoryIds = categoryIds;
    }
}

