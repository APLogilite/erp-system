package com.erp.modules.crm.dto;

import java.time.LocalDate;
import java.util.UUID;

public class OpportunityRequest {
  private UUID businessPartnerId;
  private Double probability;
  private Double expectedRevenue;
  private LocalDate expectedCloseDate;
  private UUID salespersonId;

  public UUID getBusinessPartnerId() { return businessPartnerId; }
  public void setBusinessPartnerId(UUID businessPartnerId) { this.businessPartnerId = businessPartnerId; }
  public Double getProbability() { return probability; }
  public void setProbability(Double probability) { this.probability = probability; }
  public Double getExpectedRevenue() { return expectedRevenue; }
  public void setExpectedRevenue(Double expectedRevenue) { this.expectedRevenue = expectedRevenue; }
  public LocalDate getExpectedCloseDate() { return expectedCloseDate; }
  public void setExpectedCloseDate(LocalDate expectedCloseDate) { this.expectedCloseDate = expectedCloseDate; }
  public UUID getSalespersonId() { return salespersonId; }
  public void setSalespersonId(UUID salespersonId) { this.salespersonId = salespersonId; }
}
