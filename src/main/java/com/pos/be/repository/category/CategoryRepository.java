package com.pos.be.repository.category;

import com.pos.be.entity.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long>, PagingAndSortingRepository<Category, Long> {

    Boolean existsByName(String name);

    Optional<Category> findByName(String name);

    @Query("""
            SELECT c, COUNT(p)
            FROM Category c 
            LEFT JOIN c.products p 
            WHERE (:query IS NULL OR c.name LIKE CONCAT('%', :query, '%'))
            GROUP BY c
            """)
    Page<Object[]> findCategoriesWithProductCounts(@Param("query") String query, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Category c")
    long count();

    @Query("SELECT SUM(SIZE(c.products)) FROM Category c")
    Long sumProductsAcrossAllCategories();


}
