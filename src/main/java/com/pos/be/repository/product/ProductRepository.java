package com.pos.be.repository.product;//package com.pos.be.repository.product;

import com.pos.be.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByNameIgnoreCase(String name);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findByCategories_Id(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "JOIN p.categories c " +
            "WHERE c.id = :categoryId " +
            "AND (:query IS NULL OR :query = '' " +
            "OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%'))) ")
    Page<Product> findByCategoryAndSearch(@Param("categoryId") Long categoryId,
                                          @Param("query") String query,
                                          Pageable pageable);

    Page<Product> findByCategories_IdAndNameContainingIgnoreCase(Long categoryId, String query, Pageable pageable);

    @Query(value = """
            SELECT
                p.product_id,
                p.name,
                p.description,
                p.price,
                p.quantity,
                GROUP_CONCAT(pc.category_id) AS category_ids
            FROM
                product p
            LEFT JOIN
                product_category pc ON p.product_id = pc.product_id
            WHERE (:name IS NULL OR p.name LIKE %:name%)
            GROUP BY p.product_id
            """, nativeQuery = true)
    Page<Object[]> findRawProductData(@Param("name") String name, Pageable pageable);

    @Query(value = """
            SELECT 
                p.product_id, 
                p.name, 
                p.description, 
                p.price, 
                GROUP_CONCAT(DISTINCT c.name) AS category_names, 
                GROUP_CONCAT(DISTINCT co.option_label) AS option_labels, 
                GROUP_CONCAT(DISTINCT co.option_type) AS option_types, 
                GROUP_CONCAT(cov.value) AS option_values 
            FROM product p 
            LEFT JOIN product_category pc ON pc.product_id = p.product_id 
            JOIN category c ON pc.category_id = c.id 
            JOIN custom_options co ON co.product_id = p.product_id 
            JOIN custom_options_values cov ON cov.custom_options_id = co.custom_options_id 
            WHERE p.product_id = :id 
            GROUP BY p.product_id, p.name, p.description, p.price
            """, nativeQuery = true)
    Object getProductDetailsById(@Param("id") Long id);
}
