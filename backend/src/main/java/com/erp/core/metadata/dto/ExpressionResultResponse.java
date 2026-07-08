package com.erp.core.metadata.dto;

public class ExpressionResultResponse {
  private boolean valid;
  private boolean passed;
  private String message;
  private Object result;

  public ExpressionResultResponse() {}

  public boolean isValid() { return valid; }
  public void setValid(boolean valid) { this.valid = valid; }
  public boolean isPassed() { return passed; }
  public void setPassed(boolean passed) { this.passed = passed; }
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }
  public Object getResult() { return result; }
  public void setResult(Object result) { this.result = result; }
}
