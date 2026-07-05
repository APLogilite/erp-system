package com.erp.modules.assets.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "assets")
public class Asset extends BaseEntity {

  @Column(name = "asset_code", nullable = false, unique = true)
  private String assetCode;

  @Column(name = "asset_name", nullable = false)
  private String assetName;

  @Column(name = "asset_type")
  private String assetType;

  @Column(name = "purchase_date")
  private LocalDate purchaseDate;

  @Column(name = "purchase_cost")
  private Double purchaseCost = 0.0;

  @Column(name = "current_value")
  private Double currentValue = 0.0;

  @Column(name = "assigned_to")
  private UUID assignedTo;

  @Column
  private String location;

  @Column(nullable = false)
  private String status = "DRAFT";

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
}
