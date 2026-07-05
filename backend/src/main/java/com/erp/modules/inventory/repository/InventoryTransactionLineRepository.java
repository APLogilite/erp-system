package com.erp.modules.inventory.repository;

import com.erp.modules.inventory.entity.InventoryTransactionLine;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryTransactionLineRepository extends JpaRepository<InventoryTransactionLine, UUID> {
  List<InventoryTransactionLine> findByTransactionId(UUID transactionId);
}
