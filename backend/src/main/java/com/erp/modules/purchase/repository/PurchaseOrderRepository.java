package com.erp.modules.purchase.repository;

import com.erp.modules.purchase.entity.PurchaseOrder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
  Optional<PurchaseOrder> findByDocumentNo(String documentNo);
  List<PurchaseOrder> findByVendorId(UUID vendorId);
  List<PurchaseOrder> findByStatus(String status);
}
