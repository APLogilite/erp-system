package com.erp.modules.crm.dto;

import java.util.UUID;

public class LeadRequest {
  private String company;
  private String contactName;
  private String email;
  private String phone;
  private String source;
  private UUID ownerId;
  private Double expectedValue;

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
  public UUID getOwnerId() { return ownerId; }
  public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
  public Double getExpectedValue() { return expectedValue; }
  public void setExpectedValue(Double expectedValue) { this.expectedValue = expectedValue; }
}
