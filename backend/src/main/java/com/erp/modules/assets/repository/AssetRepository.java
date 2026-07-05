package com.erp.modules.assets.repository;

import com.erp.modules.assets.entity.Asset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, UUID> {
  Optional<Asset> findByAssetCode(String assetCode);
  List<Asset> findByAssignedTo(UUID employeeId);
  List<Asset> findByStatus(String status);
  List<Asset> findByAssetType(String assetType);
}
