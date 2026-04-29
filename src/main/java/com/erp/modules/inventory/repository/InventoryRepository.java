package com.erp.modules.inventory.repository;

import com.erp.modules.inventory.entity.InventoryEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, UUID> {
}
