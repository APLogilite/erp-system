package com.erp.modules.assets.dto;

import java.time.LocalDate;
import java.util.UUID;

public class AssetRequest {
  private String assetCode;
  private String assetName;
  private String assetType;
  private LocalDate purchaseDate;
  private Double purchaseCost;
  private UUID assignedTo;
  private String location;

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
  public UUID getAssignedTo() { return assignedTo; }
  public void setAssignedTo(UUID assignedTo) { this.assignedTo = assignedTo; }
  public String getLocation() { return location; }
  public void setLocation(String location) { this.location = location; }
}
