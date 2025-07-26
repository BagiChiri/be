package com.pos.be.repository.order;

import com.pos.be.entity.order.Consignment;
import com.pos.be.entity.order.ConsignmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsignmentRepository extends JpaRepository<Consignment, Long>, JpaSpecificationExecutor<Consignment> {
    List<Consignment> consignmentId(Long consignmentId);

    @Query("SELECT COUNT(ci) > 0 FROM ConsignmentItem ci WHERE ci.product.product_id = ?1")
    boolean existsConsignmentByProductId(Long productId);

    long countByConsignmentStatus(ConsignmentStatus consignmentStatus);

    Optional<Consignment> findByConsignmentNumber(String consignmentNumber);

    Page<Consignment> findByCustomerNameContainingIgnoreCase(
            String customerName,
            Pageable pageable
    );

    @Query("""
              SELECT 
                CASE 
                  WHEN :interval = 'daily'   THEN FUNCTION('DATE_FORMAT', c.consignmentDate, '%Y-%m-%d')
                  WHEN :interval = 'monthly' THEN FUNCTION('DATE_FORMAT', c.consignmentDate, '%Y-%m')
                  WHEN :interval = 'yearly'  THEN FUNCTION('DATE_FORMAT', c.consignmentDate, '%Y')
                END,
                c.consignmentStatus,
                COUNT(c)
              FROM Consignment c
              WHERE c.consignmentDate >= :fromDate
                AND c.consignmentDate <  :toDate
                AND c.consignmentStatus IS NOT NULL
              GROUP BY 
                CASE 
                  WHEN :interval = 'daily'   THEN FUNCTION('DATE_FORMAT', c.consignmentDate, '%Y-%m-%d')
                  WHEN :interval = 'monthly' THEN FUNCTION('DATE_FORMAT', c.consignmentDate, '%Y-%m')
                  WHEN :interval = 'yearly'  THEN FUNCTION('DATE_FORMAT', c.consignmentDate, '%Y')
                END,
                c.consignmentStatus
              ORDER BY 
                CASE 
                  WHEN :interval = 'daily'   THEN FUNCTION('DATE_FORMAT', c.consignmentDate, '%Y-%m-%d')
                  WHEN :interval = 'monthly' THEN FUNCTION('DATE_FORMAT', c.consignmentDate, '%Y-%m')
                  WHEN :interval = 'yearly'  THEN FUNCTION('DATE_FORMAT', c.consignmentDate, '%Y')
                END
            """)
    List<Object[]> countConsignmentsByStatusInterval(
            @Param("interval") String interval,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
