package com.pos.be.repository.order;

import com.pos.be.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> id(Long id);
    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.product.id = ?1")
    boolean existsOrderItemByProductId(Long productId);

}
