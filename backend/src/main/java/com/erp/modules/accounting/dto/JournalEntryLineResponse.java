package com.erp.modules.accounting.dto;

import java.util.UUID;

public class JournalEntryLineResponse {

  private UUID id;
  private UUID journalEntryId;
  private Integer lineNo;
  private UUID accountId;
  private String description;
  private Double debit;
  private Double credit;
  private UUID businessPartnerId;
  private UUID productId;
  private String costCenter;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public UUID getJournalEntryId() { return journalEntryId; }
  public void setJournalEntryId(UUID journalEntryId) { this.journalEntryId = journalEntryId; }
  public Integer getLineNo() { return lineNo; }
  public void setLineNo(Integer lineNo) { this.lineNo = lineNo; }
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
