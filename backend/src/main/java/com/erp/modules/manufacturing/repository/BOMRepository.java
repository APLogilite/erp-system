package com.erp.modules.manufacturing.repository;

import com.erp.modules.manufacturing.entity.BillOfMaterial;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BOMRepository extends JpaRepository<BillOfMaterial, UUID> {
  Optional<BillOfMaterial> findByCode(String code);
  List<BillOfMaterial> findByProductId(UUID productId);
  List<BillOfMaterial> findByStatus(String status);
  List<BillOfMaterial> findByProductIdAndStatus(UUID productId, String status);
}
