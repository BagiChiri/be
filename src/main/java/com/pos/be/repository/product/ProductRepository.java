package com.pos.be.repository.product;

import com.pos.be.entity.product.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {
    @Query(
            value = "select " +
                    "    p.product_id, " +
                    "    p.name, " +
                    "    p.description, " +
                    "    p.price, " +
                    "    GROUP_CONCAT(distinct c.name) as category_names, " +
                    "    GROUP_CONCAT(distinct co.option_label) as option_labels, " +
                    "    GROUP_CONCAT(distinct co.option_type) as option_types, " +
                    "    GROUP_CONCAT(cov.value) as option_values " +
                    " " +
                    "from product p " +
                    "         left join product_category pc on pc.product_id = p.product_id " +
                    "         join category c on pc.category_id = c.id " +
                    "         join custom_options co on co.product_id = p.product_id " +
                    "         join custom_options_values cov on cov.custom_options_id = co.custom_options_id " +
                    "where p.product_id = :id " +
                    "group by p.product_id, p.name, p.description, p.price"
            , nativeQuery = true)
    Object getProductDetailsById(@Param("id") Long id);
    @Query(
            """
                    select p.id as id, p.name as name, p.price as price, p.quantity as quantity from Product p where p.id = 3
                    """
    )
    Object getProductIntro();
}