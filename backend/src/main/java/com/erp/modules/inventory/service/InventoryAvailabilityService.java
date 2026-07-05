package com.erp.modules.inventory.service;

import com.erp.modules.inventory.entity.InventoryBalance;
import com.erp.modules.inventory.entity.StockMovement;
import com.erp.modules.inventory.repository.InventoryBalanceRepository;
import com.erp.modules.inventory.repository.InventoryTransactionLineRepository;
import com.erp.modules.inventory.repository.StockMovementRepository;
import com.erp.modules.reservation.service.ReservationService;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryAvailabilityService {

  private final StockMovementRepository stockMovementRepository;
  private final InventoryBalanceRepository balanceRepository;
  private final ReservationService reservationService;

  public InventoryAvailabilityService(
      StockMovementRepository stockMovementRepository,
      InventoryBalanceRepository balanceRepository,
      ReservationService reservationService) {
    this.stockMovementRepository = stockMovementRepository;
    this.balanceRepository = balanceRepository;
    this.reservationService = reservationService;
  }

  public Double getOnHand(UUID productId, UUID warehouseId) {
    InventoryBalance balance = balanceRepository
        .findByProductIdAndWarehouseId(productId, warehouseId).orElse(null);
    if (balance != null) {
      return balance.getOnHand();
    }
    return stockMovementRepository.findByProductId(productId).stream()
        .filter(m -> m.getWarehouseId().equals(warehouseId))
        .mapToDouble(StockMovement::getQuantity)
        .sum();
  }

  public Double getReserved(UUID productId, UUID warehouseId) {
    InventoryBalance balance = balanceRepository
        .findByProductIdAndWarehouseId(productId, warehouseId).orElse(null);
    if (balance != null) {
      return balance.getReserved();
    }
    return reservationService.getTotalReserved(productId, warehouseId);
  }

  public Double getAvailable(UUID productId, UUID warehouseId) {
    return getOnHand(productId, warehouseId) - getReserved(productId, warehouseId);
  }

  public Double getIncoming(UUID productId, UUID warehouseId) {
    return stockMovementRepository.findByProductId(productId).stream()
        .filter(m -> m.getWarehouseId().equals(warehouseId))
        .filter(m -> "PURCHASE".equals(m.getMovementType()) || "IN".equals(m.getMovementType()))
        .filter(m -> m.getQuantity() > 0)
        .mapToDouble(StockMovement::getQuantity)
        .sum();
  }

  public Double getOutgoing(UUID productId, UUID warehouseId) {
    return stockMovementRepository.findByProductId(productId).stream()
        .filter(m -> m.getWarehouseId().equals(warehouseId))
        .filter(m -> "SALE".equals(m.getMovementType()) || "OUT".equals(m.getMovementType()))
        .filter(m -> m.getQuantity() < 0)
        .mapToDouble(m -> -m.getQuantity())
        .sum();
  }

  public Double getATP(UUID productId, UUID warehouseId) {
    return getOnHand(productId, warehouseId)
        - getReserved(productId, warehouseId)
        + getIncoming(productId, warehouseId)
        - getOutgoing(productId, warehouseId);
  }

  @Transactional
  public InventoryBalance updateBalance(UUID productId, UUID warehouseId) {
    InventoryBalance balance = balanceRepository
        .findByProductIdAndWarehouseId(productId, warehouseId)
        .orElse(new InventoryBalance());

    balance.setProductId(productId);
    balance.setWarehouseId(warehouseId);
    balance.setOnHand(getOnHand(productId, warehouseId));
    balance.setReserved(getReserved(productId, warehouseId));
    balance.setAvailable(balance.getOnHand() - balance.getReserved());

    return balanceRepository.save(balance);
  }
}
