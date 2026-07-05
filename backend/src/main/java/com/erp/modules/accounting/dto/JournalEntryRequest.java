package com.erp.modules.accounting.dto;

import java.time.LocalDate;
import java.util.List;

public class JournalEntryRequest {

  private LocalDate documentDate;
  private String description;
  private List<JournalEntryLineRequest> lines;

  public LocalDate getDocumentDate() { return documentDate; }
  public void setDocumentDate(LocalDate documentDate) { this.documentDate = documentDate; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public List<JournalEntryLineRequest> getLines() { return lines; }
  public void setLines(List<JournalEntryLineRequest> lines) { this.lines = lines; }
}
