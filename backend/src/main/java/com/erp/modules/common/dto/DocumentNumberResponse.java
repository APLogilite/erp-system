package com.erp.modules.common.dto;

/**
 * Response DTO for document sequence endpoints.
 */
public class DocumentNumberResponse {

  private String documentNumber;
  private String nextNumber;

  public DocumentNumberResponse() {}

  public DocumentNumberResponse(String documentNumber) {
    this.documentNumber = documentNumber;
  }

  public String getDocumentNumber() { return documentNumber; }
  public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

  public String getNextNumber() { return nextNumber; }
  public void setNextNumber(String nextNumber) { this.nextNumber = nextNumber; }
}
