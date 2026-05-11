package com.erp.modules.order.repository;

import com.erp.modules.order.entity.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Order repository.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByIsActiveTrue();
    List<Order> findByOrderType(String orderType);
}
