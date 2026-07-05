package com.erp.modules.inventory.service;

import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * StockMovementService handles inventory balance updates.
 * Supports: increase stock, decrease stock, transfer, adjust.
 */
@Service
public class StockMovementService {

  public void increaseStock(UUID productId, UUID warehouseId, Double quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive for stock increase");
    }
  }

  public void decreaseStock(UUID productId, UUID warehouseId, Double quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive for stock decrease");
    }
  }

  public void transferStock(UUID productId, UUID fromWarehouse, UUID toWarehouse, Double quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive for stock transfer");
    }
    decreaseStock(productId, fromWarehouse, quantity);
    increaseStock(productId, toWarehouse, quantity);
  }

  public void adjustStock(UUID productId, UUID warehouseId, Double adjustment) {
    if (adjustment == 0) {
      throw new IllegalArgumentException("Adjustment quantity cannot be zero");
    }
  }
}
