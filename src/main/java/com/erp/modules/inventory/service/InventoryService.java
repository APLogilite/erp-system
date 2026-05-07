package com.erp.modules.inventory.service;

import com.erp.common.base.BaseService;
import com.erp.modules.inventory.entity.StockMovement;
import com.erp.modules.inventory.repository.StockMovementRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * Inventory service for stock movements.
 */
@Service
public class InventoryService extends BaseService<StockMovement> {

  private final StockMovementRepository stockMovementRepository;

  public InventoryService(StockMovementRepository stockMovementRepository) {
    this.stockMovementRepository = stockMovementRepository;
  }

  @Override
  protected JpaRepository<StockMovement, UUID> getRepository() {
    return stockMovementRepository;
  }

  /**
   * Get current stock for a product in a warehouse.
   * Stock is derived from SUM(quantity) of movements.
   */
  public Double getCurrentStock(UUID productId, UUID warehouseId) {
    List<StockMovement> movements = stockMovementRepository.findByProductId(productId);
    return movements.stream()
        .filter(m -> m.getWarehouseId().equals(warehouseId))
        .mapToDouble(StockMovement::getQuantity)
        .sum();
  }
}
