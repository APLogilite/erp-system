package com.erp.modules.warehouse.repository;

import com.erp.modules.warehouse.entity.Location;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    List<Location> findByWarehouseId(UUID warehouseId);
    List<Location> findByParentId(UUID parentId);
    List<Location> findByIsActiveTrue();
}
