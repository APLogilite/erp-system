package com.erp.core.metadata.dto;

import java.util.Map;

public class ExpressionEvaluateRequest {
  private String expression;
  private Map<String, Object> sampleData;

  public ExpressionEvaluateRequest() {}

  public String getExpression() { return expression; }
  public void setExpression(String expression) { this.expression = expression; }
  public Map<String, Object> getSampleData() { return sampleData; }
  public void setSampleData(Map<String, Object> sampleData) { this.sampleData = sampleData; }
}
