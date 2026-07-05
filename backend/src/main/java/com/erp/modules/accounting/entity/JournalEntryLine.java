package com.erp.modules.accounting.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "journal_entry_lines")
public class JournalEntryLine extends BaseEntity {

  @Column(name = "journal_entry_id", nullable = false)
  private UUID journalEntryId;

  @Column(name = "line_no")
  private Integer lineNo;

  @Column(name = "account_id", nullable = false)
  private UUID accountId;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column
  private Double debit = 0.0;

  @Column
  private Double credit = 0.0;

  @Column(name = "business_partner_id")
  private UUID businessPartnerId;

  @Column(name = "product_id")
  private UUID productId;

  @Column(name = "cost_center")
  private String costCenter;

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
