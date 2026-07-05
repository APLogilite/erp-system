package com.erp.modules.accounting.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AccountResponse {

  private UUID id;
  private String accountCode;
  private String name;
  private String description;
  private String accountType;
  private UUID parentId;
  private String currency;
  private Boolean isControlAccount;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getAccountCode() { return accountCode; }
  public void setAccountCode(String accountCode) { this.accountCode = accountCode; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getAccountType() { return accountType; }
  public void setAccountType(String accountType) { this.accountType = accountType; }
  public UUID getParentId() { return parentId; }
  public void setParentId(UUID parentId) { this.parentId = parentId; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public Boolean getIsControlAccount() { return isControlAccount; }
  public void setIsControlAccount(Boolean isControlAccount) { this.isControlAccount = isControlAccount; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
