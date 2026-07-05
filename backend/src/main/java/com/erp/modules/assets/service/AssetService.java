package com.erp.modules.assets.service;

import com.erp.common.base.BaseService;
import com.erp.modules.assets.entity.Asset;
import com.erp.modules.assets.repository.AssetRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssetService extends BaseService<Asset> {

  private final AssetRepository assetRepository;

  public AssetService(AssetRepository assetRepository) {
    this.assetRepository = assetRepository;
  }

  @Override
  protected JpaRepository<Asset, UUID> getRepository() {
    return assetRepository;
  }

  @Override
  protected void beforeCreate(Asset entity) {
    if (entity.getAssetCode() == null || entity.getAssetCode().trim().isEmpty()) {
      throw new IllegalArgumentException("Asset code is required");
    }
    if (entity.getStatus() == null) {
      entity.setStatus("DRAFT");
    }
    if (entity.getCurrentValue() == null) {
      entity.setCurrentValue(entity.getPurchaseCost() != null ? entity.getPurchaseCost() : 0.0);
    }
    if (assetRepository.findByAssetCode(entity.getAssetCode()).isPresent()) {
      throw new IllegalArgumentException("Asset code must be unique");
    }
  }

  @Override
  protected void beforeUpdate(Asset newEntity, Asset existingEntity) {
    if ("DISPOSED".equals(existingEntity.getStatus())) {
      throw new IllegalArgumentException("Cannot modify a DISPOSED asset");
    }
    if (!newEntity.getAssetCode().equals(existingEntity.getAssetCode())
        && assetRepository.findByAssetCode(newEntity.getAssetCode()).isPresent()) {
      throw new IllegalArgumentException("Asset code must be unique");
    }
  }

  @Transactional
  public UUID activate(UUID assetId) {
    Asset asset = findByIdOrThrow(assetId);
    if (!"DRAFT".equals(asset.getStatus())) {
      throw new IllegalArgumentException("Only DRAFT assets can be activated");
    }
    asset.setStatus("ACTIVE");
    return assetRepository.save(asset).getId();
  }

  @Transactional
  public UUID assign(UUID assetId, UUID employeeId) {
    Asset asset = findByIdOrThrow(assetId);
    if ("DISPOSED".equals(asset.getStatus())) {
      throw new IllegalArgumentException("Cannot assign a DISPOSED asset");
    }
    asset.setAssignedTo(employeeId);
    if ("DRAFT".equals(asset.getStatus())) {
      asset.setStatus("ACTIVE");
    }
    return assetRepository.save(asset).getId();
  }

  @Transactional
  public UUID maintain(UUID assetId) {
    Asset asset = findByIdOrThrow(assetId);
    if (!"ACTIVE".equals(asset.getStatus())) {
      throw new IllegalArgumentException("Only ACTIVE assets can be sent to maintenance");
    }
    asset.setStatus("MAINTENANCE");
    return assetRepository.save(asset).getId();
  }

  @Transactional
  public UUID dispose(UUID assetId) {
    Asset asset = findByIdOrThrow(assetId);
    if ("DISPOSED".equals(asset.getStatus())) {
      throw new IllegalArgumentException("Asset is already DISPOSED");
    }
    asset.setStatus("DISPOSED");
    asset.setCurrentValue(0.0);
    return assetRepository.save(asset).getId();
  }
}
