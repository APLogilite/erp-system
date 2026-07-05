package com.erp.core.metadata.exception;

import java.util.List;

public class MetadataValidationException extends RuntimeException {

  private List<String> validationErrors;

  public MetadataValidationException(String message) {
    super(message);
  }

  public MetadataValidationException(String message, List<String> validationErrors) {
    super(message);
    this.validationErrors = validationErrors;
  }

  public MetadataValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  public List<String> getValidationErrors() {
    return validationErrors;
  }
}
