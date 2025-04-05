package com.pos.be.repository.product;//package com.pos.be.repository.product;
//
//import com.pos.be.entity.product.Product;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.data.repository.PagingAndSortingRepository;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface ProductRepository extends JpaRepository<Product, Long>, CrudRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {
//
//    public Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
//
//    @Query(
//            value = "select " +
//                    "    p.product_id, " +
//                    "    p.name, " +
//                    "    p.description, " +
//                    "    p.price, " +
//                    "    GROUP_CONCAT(distinct c.name) as category_names, " +
//                    "    GROUP_CONCAT(distinct co.option_label) as option_labels, " +
//                    "    GROUP_CONCAT(distinct co.option_type) as option_types, " +
//                    "    GROUP_CONCAT(cov.value) as option_values " +
//                    " " +
//                    "from product p " +
//                    "         left join product_category pc on pc.product_id = p.product_id " +
//                    "         join category c on pc.category_id = c.id " +
//                    "         join custom_options co on co.product_id = p.product_id " +
//                    "         join custom_options_values cov on cov.custom_options_id = co.custom_options_id " +
//                    "where p.product_id = :id " +
//                    "group by p.product_id, p.name, p.description, p.price"
//            , nativeQuery = true)
//    Object getProductDetailsById(@Param("id") Long id);
//
//    @Query(
//            """
//                    select p.id as id, p.name as name, p.price as price, p.quantity as quantity from Product p where p.id = 3
//                    """
//    )
//    Object getProductIntro();
//
//    @Query(value = """
//            SELECT
//                p.product_id,
//                p.name,
//                p.description,
//                p.price,
//                p.quantity,
//                GROUP_CONCAT(pc.category_id) AS category_ids
//            FROM
//                Product p
//            LEFT JOIN
//                Product_Category pc
//            ON
//                p.product_id = pc.product_id
//            WHERE (:name IS NULL OR p.name LIKE %:name%)
//            GROUP BY
//                p.product_id
//            """,
//            countQuery = """
//            SELECT COUNT(DISTINCT p.product_id)
//            FROM
//                Product p
//            LEFT JOIN
//                Product_Category pc
//            ON
//                p.product_id = pc.product_id
//            WHERE (:name IS NULL OR p.name LIKE %:name%)
//            """,
//            nativeQuery = true)
//    Page<Object[]> findRawProductData(@Param("name") String name, Pageable pageable);
//
//}

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
