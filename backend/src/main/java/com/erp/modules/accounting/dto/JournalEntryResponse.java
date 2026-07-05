package com.erp.modules.accounting.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class JournalEntryResponse {

  private UUID id;
  private String documentNo;
  private LocalDate documentDate;
  private String description;
  private String status;
  private Double totalDebit;
  private Double totalCredit;
  private List<JournalEntryLineResponse> lines;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Boolean isActive;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
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
  public List<JournalEntryLineResponse> getLines() { return lines; }
  public void setLines(List<JournalEntryLineResponse> lines) { this.lines = lines; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
