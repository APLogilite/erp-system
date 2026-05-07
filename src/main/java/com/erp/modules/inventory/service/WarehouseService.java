package com.erp.modules.inventory.service;

import com.erp.common.base.BaseService;
import com.erp.modules.inventory.entity.Warehouse;
import com.erp.modules.inventory.repository.WarehouseRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * Warehouse service.
 */
@Service
public class WarehouseService extends BaseService<Warehouse> {

  private final WarehouseRepository warehouseRepository;

  public WarehouseService(WarehouseRepository warehouseRepository) {
    this.warehouseRepository = warehouseRepository;
  }

  @Override
  protected JpaRepository<Warehouse, UUID> getRepository() {
    return warehouseRepository;
  }
}