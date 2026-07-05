package com.erp.modules.accounting.dto;

import java.util.UUID;

public class JournalEntryLineRequest {

  private UUID accountId;
  private String description;
  private Double debit;
  private Double credit;
  private UUID businessPartnerId;
  private UUID productId;
  private String costCenter;

  public UUID getAccountId() { return accountId; }
  public void setAccountId(UUID accountId) { this.accountId = accountId; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public Double getDebit() { return debit; }
  public void setDebit(Double debit) { this.debit = debit; }
  public Double getCredit() { return credit; }
  public void setCredit(Double credit) { this.credit = credit; }
  public UUID getBusinessPartnerId() { return businessPartnerId; }
  public void setBusinessPartnerId(UUID businessPartnerId) { this.businessPartnerId = businessPartnerId; }
  public UUID getProductId() { return productId; }
  public void setProductId(UUID productId) { this.productId = productId; }
  public String getCostCenter() { return costCenter; }
  public void setCostCenter(String costCenter) { this.costCenter = costCenter; }
}
