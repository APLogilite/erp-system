package com.erp.modules.inventory.service;

import com.erp.modules.inventory.entity.InventoryBalance;
import com.erp.modules.inventory.entity.StockMovement;
import com.erp.modules.inventory.repository.InventoryBalanceRepository;
import com.erp.modules.inventory.repository.StockMovementRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AllocationService {

  private final InventoryBalanceRepository balanceRepository;
  private final StockMovementRepository stockMovementRepository;

  public AllocationService(
      InventoryBalanceRepository balanceRepository,
      StockMovementRepository stockMovementRepository) {
    this.balanceRepository = balanceRepository;
    this.stockMovementRepository = stockMovementRepository;
  }

  @Transactional
  public void allocate(UUID productId, UUID warehouseId, Double quantity, UUID referenceId) {
    InventoryBalance balance = balanceRepository
        .findByProductIdAndWarehouseId(productId, warehouseId)
        .orElseThrow(() -> new IllegalArgumentException("No inventory balance found"));

    if (balance.getAvailable() < quantity) {
      throw new IllegalArgumentException("Insufficient available inventory");
    }

    balance.setReserved(balance.getReserved() + quantity);
    balance.setAvailable(balance.getOnHand() - balance.getReserved());
    balanceRepository.save(balance);

    StockMovement movement = new StockMovement();
    movement.setProductId(productId);
    movement.setWarehouseId(warehouseId);
    movement.setQuantity(-quantity);
    movement.setMovementType("ALLOCATION");
    movement.setReferenceType("ALLOCATION");
    movement.setReferenceId(referenceId);
    movement.setMovementDate(LocalDateTime.now());
    stockMovementRepository.save(movement);
  }

  @Transactional
  public void deallocate(UUID productId, UUID warehouseId, Double quantity, UUID referenceId) {
    InventoryBalance balance = balanceRepository
        .findByProductIdAndWarehouseId(productId, warehouseId)
        .orElseThrow(() -> new IllegalArgumentException("No inventory balance found"));

    balance.setReserved(balance.getReserved() - quantity);
    balance.setAvailable(balance.getOnHand() - balance.getReserved());
    balanceRepository.save(balance);
  }

  @Transactional
  public void reallocate(UUID productId, UUID fromWarehouse, UUID toWarehouse,
                          Double quantity, UUID referenceId) {
    deallocate(productId, fromWarehouse, quantity, referenceId);
    allocate(productId, toWarehouse, quantity, referenceId);
  }

  @Transactional
  public void allocateFIFO(UUID productId, UUID warehouseId, Double quantity, UUID referenceId) {
    List<StockMovement> inboundMovements = stockMovementRepository.findByProductId(productId).stream()
        .filter(m -> m.getWarehouseId().equals(warehouseId))
        .filter(m -> m.getQuantity() > 0)
        .sorted(Comparator.comparing(StockMovement::getMovementDate))
        .toList();

    double remaining = quantity;
    for (StockMovement movement : inboundMovements) {
      if (remaining <= 0) break;
      double available = movement.getQuantity();
      double toAllocate = Math.min(available, remaining);
      remaining -= toAllocate;
    }

    if (remaining > 0) {
      throw new IllegalArgumentException("Insufficient stock for FIFO allocation");
    }

    allocate(productId, warehouseId, quantity, referenceId);
  }
}
