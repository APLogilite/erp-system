package com.erp.modules.inventory.service;

import com.erp.common.base.BaseService;
import com.erp.modules.inventory.entity.InventoryEntity;
import com.erp.modules.inventory.repository.InventoryRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * Inventory service placeholder.
 * Business logic is intentionally not implemented.
 */
@Service
public class InventoryService extends BaseService<InventoryEntity> {

  private final InventoryRepository inventoryRepository;

  public InventoryService(InventoryRepository inventoryRepository) {
    this.inventoryRepository = inventoryRepository;
  }

  @Override
  protected JpaRepository<InventoryEntity, UUID> getRepository() {
    return inventoryRepository;
  }
}
