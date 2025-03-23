package com.pos.be.repository.order;

import com.pos.be.entity.order.ConsignmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsignmentItemRepository extends JpaRepository<ConsignmentItem, Long> {
    @Query("SELECT CASE WHEN COUNT(ci) > 0 THEN true ELSE false END FROM ConsignmentItem ci WHERE ci.product.product_id = :productId")
    boolean existsByProductId(@Param("productId") Long productId);
}
