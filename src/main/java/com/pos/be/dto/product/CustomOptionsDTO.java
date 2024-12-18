package com.pos.be.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CustomOptionsDTO {
    @Positive(message = "Product Id Must Be Positive.")
    private Long id;
    @NotNull(message = "Custom Option Option Label Must Not Be Null.")
    @NotBlank(message = "Custom Option Option Label Must Not Be Blank.")
    private String optionLabel;
    @NotNull(message = "Custom Option Option Type Must Not Be Null.")
    @NotBlank(message = "Custom Option Option Type Must Not Be Blank.")
    private String optionType;
    @NotNull(message = "Custom Option value Required.")
    private List<String> optionValue;
    @NotNull(message = "Product Id value Required.")
    @Positive(message = "Product Id Must Be Positive.")
    private Long productId;
}
