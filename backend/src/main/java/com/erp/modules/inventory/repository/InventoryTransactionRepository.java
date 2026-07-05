package com.erp.modules.inventory.repository;

import com.erp.modules.inventory.entity.InventoryTransaction;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, UUID> {
  Optional<InventoryTransaction> findByDocumentNumber(String documentNumber);

  List<InventoryTransaction> findByWarehouseId(UUID warehouseId);

  List<InventoryTransaction> findByTransactionType(String transactionType);

  List<InventoryTransaction> findByStatus(String status);
}
