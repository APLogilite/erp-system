package com.erp.modules.manufacturing.repository;

import com.erp.modules.manufacturing.entity.WorkOrder;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID> {
  List<WorkOrder> findByManufacturingOrderIdOrderBySequenceAsc(UUID manufacturingOrderId);
  List<WorkOrder> findByWorkCenterId(UUID workCenterId);
  List<WorkOrder> findByStatus(String status);
}
