package com.erp.modules.accounting.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "journal_entries")
public class JournalEntry extends BaseEntity {

  @Column(name = "document_no", unique = true, nullable = false)
  private String documentNo;

  @Column(name = "document_date", nullable = false)
  private LocalDate documentDate;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private String status = "DRAFT";

  @Column(name = "total_debit")
  private Double totalDebit = 0.0;

  @Column(name = "total_credit")
  private Double totalCredit = 0.0;

  public String getDocumentNo() { return documentNo; }
  public void setDocumentNo(String documentNo) { this.documentNo = documentNo; }
  public LocalDate getDocumentDate() { return documentDate; }
  public void setDocumentDate(LocalDate documentDate) { this.documentDate = documentDate; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Double getTotalDebit() { return totalDebit; }
  public void setTotalDebit(Double totalDebit) { this.totalDebit = totalDebit; }
  public Double getTotalCredit() { return totalCredit; }
  public void setTotalCredit(Double totalCredit) { this.totalCredit = totalCredit; }
}
