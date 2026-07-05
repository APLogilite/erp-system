package com.erp.modules.manufacturing.repository;

import com.erp.modules.manufacturing.entity.ManufacturingOrder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManufacturingOrderRepository extends JpaRepository<ManufacturingOrder, UUID> {
  Optional<ManufacturingOrder> findByDocumentNo(String documentNo);
  List<ManufacturingOrder> findByProductId(UUID productId);
  List<ManufacturingOrder> findByStatus(String status);
  List<ManufacturingOrder> findByStatusIn(List<String> statuses);
}
