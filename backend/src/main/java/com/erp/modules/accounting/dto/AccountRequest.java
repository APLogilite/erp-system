package com.erp.modules.accounting.dto;

import java.util.UUID;

public class AccountRequest {

  private String accountCode;
  private String name;
  private String description;
  private String accountType;
  private UUID parentId;
  private String currency;
  private Boolean isControlAccount;

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
}
