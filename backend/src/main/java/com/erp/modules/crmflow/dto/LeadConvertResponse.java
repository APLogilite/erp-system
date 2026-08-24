package com.erp.modules.crmflow.dto;

public class LeadConvertResponse {

  private String partnerId;
  private String opportunityId;
  private String opportunityNumber;

  public LeadConvertResponse() {
  }

  public LeadConvertResponse(String partnerId, String opportunityId, String opportunityNumber) {
    this.partnerId = partnerId;
    this.opportunityId = opportunityId;
    this.opportunityNumber = opportunityNumber;
  }

  public String getPartnerId() {
    return partnerId;
  }

  public void setPartnerId(String partnerId) {
    this.partnerId = partnerId;
  }

  public String getOpportunityId() {
    return opportunityId;
  }

  public void setOpportunityId(String opportunityId) {
    this.opportunityId = opportunityId;
  }

  public String getOpportunityNumber() {
    return opportunityNumber;
  }

  public void setOpportunityNumber(String opportunityNumber) {
    this.opportunityNumber = opportunityNumber;
  }
}
