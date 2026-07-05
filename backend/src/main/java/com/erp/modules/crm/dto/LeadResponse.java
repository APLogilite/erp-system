package com.erp.modules.crm.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class LeadResponse {
  private UUID id;
  private String leadNumber;
  private String company;
  private String contactName;
  private String email;
  private String phone;
  private String source;
  private String status;
  private UUID ownerId;
  private Double expectedValue;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getLeadNumber() { return leadNumber; }
  public void setLeadNumber(String leadNumber) { this.leadNumber = leadNumber; }
  public String getCompany() { return company; }
  public void setCompany(String company) { this.company = company; }
  public String getContactName() { return contactName; }
  public void setContactName(String contactName) { this.contactName = contactName; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public String getSource() { return source; }
  public void setSource(String source) { this.source = source; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public UUID getOwnerId() { return ownerId; }
  public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
  public Double getExpectedValue() { return expectedValue; }
  public void setExpectedValue(Double expectedValue) { this.expectedValue = expectedValue; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
