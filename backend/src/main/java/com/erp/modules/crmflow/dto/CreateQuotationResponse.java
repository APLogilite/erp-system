package com.erp.modules.crmflow.dto;

public class CreateQuotationResponse {

  private String quotationId;
  private String quoteNumber;

  public CreateQuotationResponse() {
  }

  public CreateQuotationResponse(String quotationId, String quoteNumber) {
    this.quotationId = quotationId;
    this.quoteNumber = quoteNumber;
  }

  public String getQuotationId() {
    return quotationId;
  }

  public void setQuotationId(String quotationId) {
    this.quotationId = quotationId;
  }

  public String getQuoteNumber() {
    return quoteNumber;
  }

  public void setQuoteNumber(String quoteNumber) {
    this.quoteNumber = quoteNumber;
  }
}
