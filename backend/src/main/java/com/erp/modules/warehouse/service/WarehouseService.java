package com.erp.modules.warehouse.service;

import com.erp.common.base.BaseService;
import com.erp.modules.warehouse.entity.Warehouse;
import com.erp.modules.warehouse.repository.WarehouseRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

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

    @Override
    protected void beforeCreate(Warehouse entity) {
        if (warehouseRepository.findByCode(entity.getCode()).isPresent()) {
            throw new IllegalArgumentException("Warehouse code must be unique");
        }
    }

    @Override
    protected void beforeUpdate(Warehouse newEntity, Warehouse existingEntity) {
        if (!newEntity.getCode().equals(existingEntity.getCode())
                && warehouseRepository.findByCode(newEntity.getCode()).isPresent()) {
            throw new IllegalArgumentException("Warehouse code must be unique");
        }
    }
}
