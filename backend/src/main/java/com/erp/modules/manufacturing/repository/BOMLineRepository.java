package com.erp.modules.manufacturing.repository;

import com.erp.modules.manufacturing.entity.BOMLine;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BOMLineRepository extends JpaRepository<BOMLine, UUID> {
  List<BOMLine> findByBomId(UUID bomId);
  List<BOMLine> findByComponentId(UUID componentId);
}
