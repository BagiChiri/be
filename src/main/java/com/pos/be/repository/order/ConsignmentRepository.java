package com.pos.be.repository.order;

import com.pos.be.entity.order.Consignment;
import com.pos.be.entity.order.ConsignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsignmentRepository extends JpaRepository<Consignment, Long>, JpaSpecificationExecutor<Consignment> {
    // No need for order-item methods here.
    List<Consignment> consignmentId(Long consignmentId);
    @Query("SELECT COUNT(ci) > 0 FROM ConsignmentItem ci WHERE ci.product.product_id = ?1")
    boolean existsConsignmentByProductId(Long productId);

    long countByConsignmentStatus(ConsignmentStatus consignmentStatus);

    Optional<Consignment> findByConsignmentNumber(String consignmentNumber);
}
