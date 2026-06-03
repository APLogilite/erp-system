package com.erp.core.metadata.exception;

public class MetadataNotFoundException extends RuntimeException {

  private String metadataCode;

  public MetadataNotFoundException(String message) {
    super(message);
  }

  public MetadataNotFoundException(String message, String metadataCode) {
    super(message);
    this.metadataCode = metadataCode;
  }

  public MetadataNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public String getMetadataCode() {
    return metadataCode;
  }
}
