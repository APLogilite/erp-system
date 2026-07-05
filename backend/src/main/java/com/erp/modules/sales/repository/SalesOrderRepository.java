package com.erp.modules.sales.repository;

import com.erp.modules.sales.entity.SalesOrder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {
  Optional<SalesOrder> findByDocumentNo(String documentNo);
  List<SalesOrder> findByCustomerId(UUID customerId);
  List<SalesOrder> findByStatus(String status);
}
