package com.erp.modules.sales.repository;

import com.erp.modules.sales.entity.SalesOrderLine;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderLineRepository extends JpaRepository<SalesOrderLine, UUID> {
  List<SalesOrderLine> findByOrderId(UUID orderId);
  void deleteByOrderId(UUID orderId);
}
