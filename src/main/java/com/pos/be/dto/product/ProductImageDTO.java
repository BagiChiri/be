package com.pos.be.dto.product;

import com.pos.be.entity.PrimaryImageFlag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO implements PrimaryImageFlag {

    private Long id;

    @NotNull(message = "Image URL must not be null.")
    @NotBlank(message = "Image URL must not be blank.")
    private String url;

    private boolean primaryImage;

    @Override
    public boolean isPrimaryImage() {
        return primaryImage;
    }

    @Override
    public void setPrimaryImage(boolean primary) {
        this.primaryImage = primary;
    }
}
