package com.erp.modules.assets.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class AssetResponse {
  private UUID id;
  private String assetCode;
  private String assetName;
  private String assetType;
  private LocalDate purchaseDate;
  private Double purchaseCost;
  private Double currentValue;
  private UUID assignedTo;
  private String location;
  private String status;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getAssetCode() { return assetCode; }
  public void setAssetCode(String assetCode) { this.assetCode = assetCode; }
  public String getAssetName() { return assetName; }
  public void setAssetName(String assetName) { this.assetName = assetName; }
  public String getAssetType() { return assetType; }
  public void setAssetType(String assetType) { this.assetType = assetType; }
  public LocalDate getPurchaseDate() { return purchaseDate; }
  public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
  public Double getPurchaseCost() { return purchaseCost; }
  public void setPurchaseCost(Double purchaseCost) { this.purchaseCost = purchaseCost; }
  public Double getCurrentValue() { return currentValue; }
  public void setCurrentValue(Double currentValue) { this.currentValue = currentValue; }
  public UUID getAssignedTo() { return assignedTo; }
  public void setAssignedTo(UUID assignedTo) { this.assignedTo = assignedTo; }
  public String getLocation() { return location; }
  public void setLocation(String location) { this.location = location; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
