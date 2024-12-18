package com.pos.be.repository.product;

import com.pos.be.entity.product.CustomOptions;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomOptionsRepository extends CrudRepository<CustomOptions, Long> {
}
