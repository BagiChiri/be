package com.pos.be.dto.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long id;

    @NotNull(message = "Name cannot be null.")
    @Size(min = 2, max = 50, message = "Name must be in between 2 to 50 characters")
    private String name;
}
