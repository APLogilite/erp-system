package com.erp.modules.purchase.repository;

import com.erp.modules.purchase.entity.PurchaseOrderLine;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLine, UUID> {
  List<PurchaseOrderLine> findByOrderId(UUID orderId);
  void deleteByOrderId(UUID orderId);
}
