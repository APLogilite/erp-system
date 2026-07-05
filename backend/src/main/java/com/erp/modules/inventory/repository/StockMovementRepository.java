package com.erp.modules.inventory.repository;

import com.erp.modules.inventory.entity.StockMovement;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
    List<StockMovement> findByProductId(UUID productId);
    List<StockMovement> findByWarehouseId(UUID warehouseId);
}