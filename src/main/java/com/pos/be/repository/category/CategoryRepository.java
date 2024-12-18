package com.pos.be.repository.category;

import com.pos.be.entity.category.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

    Boolean existsByName(String name);

    Optional<Category> findByName(String name);
}
