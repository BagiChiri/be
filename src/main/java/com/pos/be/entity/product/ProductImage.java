package com.pos.be.entity.product;

import com.pos.be.entity.PrimaryImageFlag;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_image")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage implements PrimaryImageFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    /**
     * Field to store the original filename coming from the upload.
     */
    private String originalFilename;

    @Column(nullable = false)
    private boolean primaryImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // Custom constructor for convenience.
    public ProductImage(@NotNull @NotBlank String url, String originalFilename, boolean primaryImage, Product product) {
        this.url = url;
        this.originalFilename = originalFilename;
        this.primaryImage = primaryImage;
        this.product = product;
    }

    @Override
    public boolean isPrimaryImage() {
        return primaryImage;
    }

    @Override
    public void setPrimaryImage(boolean primary) {
        this.primaryImage = primary;
    }
}
