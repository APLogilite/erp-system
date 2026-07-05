package com.erp.modules.inventory.service;

import com.erp.modules.inventory.entity.StockMovement;
import com.erp.modules.inventory.repository.StockMovementRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockMovementService {

  private final StockMovementRepository stockMovementRepository;

  public StockMovementService(StockMovementRepository stockMovementRepository) {
    this.stockMovementRepository = stockMovementRepository;
  }

  @Transactional
  public void increaseStock(UUID productId, UUID warehouseId, Double quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive for stock increase");
    }
    StockMovement movement = new StockMovement();
    movement.setProductId(productId);
    movement.setWarehouseId(warehouseId);
    movement.setQuantity(quantity);
    movement.setMovementType("IN");
    movement.setReferenceType("TRANSACTION");
    movement.setMovementDate(LocalDateTime.now());
    stockMovementRepository.save(movement);
  }

  @Transactional
  public void decreaseStock(UUID productId, UUID warehouseId, Double quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive for stock decrease");
    }
    StockMovement movement = new StockMovement();
    movement.setProductId(productId);
    movement.setWarehouseId(warehouseId);
    movement.setQuantity(-quantity);
    movement.setMovementType("OUT");
    movement.setReferenceType("TRANSACTION");
    movement.setMovementDate(LocalDateTime.now());
    stockMovementRepository.save(movement);
  }

  @Transactional
  public void transferStock(UUID productId, UUID fromWarehouse, UUID toWarehouse, Double quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be positive for stock transfer");
    }
    decreaseStock(productId, fromWarehouse, quantity);
    increaseStock(productId, toWarehouse, quantity);
  }

  @Transactional
  public void adjustStock(UUID productId, UUID warehouseId, Double adjustment) {
    if (adjustment == 0) {
      throw new IllegalArgumentException("Adjustment quantity cannot be zero");
    }
    StockMovement movement = new StockMovement();
    movement.setProductId(productId);
    movement.setWarehouseId(warehouseId);
    movement.setQuantity(adjustment);
    movement.setMovementType("ADJUSTMENT");
    movement.setReferenceType("TRANSACTION");
    movement.setMovementDate(LocalDateTime.now());
    stockMovementRepository.save(movement);
  }
}
