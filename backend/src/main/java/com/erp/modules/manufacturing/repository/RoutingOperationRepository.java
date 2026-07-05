package com.erp.modules.manufacturing.repository;

import com.erp.modules.manufacturing.entity.RoutingOperation;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoutingOperationRepository extends JpaRepository<RoutingOperation, UUID> {
  List<RoutingOperation> findByRoutingIdOrderBySequenceAsc(UUID routingId);
  List<RoutingOperation> findByWorkCenterId(UUID workCenterId);
}
