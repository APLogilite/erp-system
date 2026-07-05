package com.erp.modules.accounting.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {

  @Column(name = "account_code", unique = true, nullable = false)
  private String accountCode;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "account_type", nullable = false)
  private String accountType;

  @Column(name = "parent_id")
  private UUID parentId;

  @Column
  private String currency = "USD";

  @Column(name = "is_control_account")
  private Boolean isControlAccount = false;

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
