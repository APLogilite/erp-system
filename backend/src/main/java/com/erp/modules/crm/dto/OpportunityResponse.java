package com.erp.modules.crm.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class OpportunityResponse {
  private UUID id;
  private String opportunityNumber;
  private UUID businessPartnerId;
  private String stage;
  private Double probability;
  private Double expectedRevenue;
  private LocalDate expectedCloseDate;
  private UUID salespersonId;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getOpportunityNumber() { return opportunityNumber; }
  public void setOpportunityNumber(String opportunityNumber) { this.opportunityNumber = opportunityNumber; }
  public UUID getBusinessPartnerId() { return businessPartnerId; }
  public void setBusinessPartnerId(UUID businessPartnerId) { this.businessPartnerId = businessPartnerId; }
  public String getStage() { return stage; }
  public void setStage(String stage) { this.stage = stage; }
  public Double getProbability() { return probability; }
  public void setProbability(Double probability) { this.probability = probability; }
  public Double getExpectedRevenue() { return expectedRevenue; }
  public void setExpectedRevenue(Double expectedRevenue) { this.expectedRevenue = expectedRevenue; }
  public LocalDate getExpectedCloseDate() { return expectedCloseDate; }
  public void setExpectedCloseDate(LocalDate expectedCloseDate) { this.expectedCloseDate = expectedCloseDate; }
  public UUID getSalespersonId() { return salespersonId; }
  public void setSalespersonId(UUID salespersonId) { this.salespersonId = salespersonId; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
