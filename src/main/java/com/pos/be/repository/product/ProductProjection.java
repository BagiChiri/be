package com.pos.be.repository.product;

public interface ProductProjection {
    Long getId();

    String getName();

    String getDescription();

    Double getPrice();

    Integer getQuantity();

    String getCategoryIds();
}
