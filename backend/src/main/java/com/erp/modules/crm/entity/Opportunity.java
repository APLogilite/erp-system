package com.erp.modules.crm.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "opportunities")
public class Opportunity extends BaseEntity {

  @Column(name = "opportunity_number", nullable = false, unique = true)
  private String opportunityNumber;

  @Column(name = "business_partner_id", nullable = false)
  private UUID businessPartnerId;

  @Column
  private String stage = "OPEN";

  @Column
  private Double probability = 0.0;

  @Column(name = "expected_revenue")
  private Double expectedRevenue = 0.0;

  @Column(name = "expected_close_date")
  private LocalDate expectedCloseDate;

  @Column(name = "salesperson_id")
  private UUID salespersonId;

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
}
