package com.pos.be.entity.product;

import com.pos.be.entity.PrimaryImageFlag;
import jakarta.persistence.*;
import lombok.*;

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

    // URL or file path for the image
    @Column(nullable = false)
    private String url;

    // Flag indicating whether this is the primary image
    @Column(nullable = false)
    private boolean primaryImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Override
    public boolean isPrimaryImage() {
        return primaryImage;
    }

    @Override
    public void setPrimaryImage(boolean primary) {
        this.primaryImage = primary;
    }
}
