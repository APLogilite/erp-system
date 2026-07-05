package com.erp.modules.crm.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "leads")
public class Lead extends BaseEntity {

  @Column(name = "lead_number", nullable = false, unique = true)
  private String leadNumber;

  @Column(nullable = false)
  private String company;

  @Column(name = "contact_name", nullable = false)
  private String contactName;

  @Column
  private String email;

  @Column
  private String phone;

  @Column
  private String source;

  @Column(nullable = false)
  private String status = "NEW";

  @Column(name = "owner_id")
  private UUID ownerId;

  @Column(name = "expected_value")
  private Double expectedValue = 0.0;

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
}
