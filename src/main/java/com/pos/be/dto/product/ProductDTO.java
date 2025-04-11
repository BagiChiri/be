//package com.pos.be.dto.product;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotEmpty;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.PositiveOrZero;
//import lombok.*;
//
//import java.util.List;
//import java.util.Set;
//
//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class ProductDTO {
//
//    private Long id;
//
//    @NotNull(message = "Product name must not be null.")
//    @NotBlank(message = "Product name must not be blank.")
//    private String name;
//
//    @NotNull(message = "Product description must not be null.")
//    @NotBlank(message = "Product description must not be blank.")
//    private String description;
//
//    @NotNull(message = "Product price required.")
//    @PositiveOrZero(message = "Product price must be positive or zero.")
//    private Double price;
//
//    private Integer quantity;
//
//    @NotNull(message = "Unit must not be null.")
//    @NotBlank(message = "Unit must not be blank.")
//    private String unit;
//
//    @NotNull(message = "Category ids must not be null.")
//    @NotEmpty(message = "At least one category id must be provided.")
//    private Set<Long> categoryIds;
//
//    private List<ProductImageDTO> images;
//}
package com.pos.be.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    @NotNull(message = "Product name must not be null.")
    @NotBlank(message = "Product name must not be blank.")
    private String name;

    @NotNull(message = "Product description must not be null.")
    @NotBlank(message = "Product description must not be blank.")
    private String description;

    @NotNull(message = "Product price required.")
    @PositiveOrZero(message = "Product price must be positive or zero.")
    private Double price;

    private Integer quantity;

    @NotNull(message = "Unit must not be null.")
    @NotBlank(message = "Unit must not be blank.")
    private String unit;

    @NotNull(message = "Category ids must not be null.")
    @NotEmpty(message = "At least one category id must be provided.")
    private Set<Long> categoryIds;

    private List<ProductImageDTO> images;
}
