package com.pos.be.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
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
}
