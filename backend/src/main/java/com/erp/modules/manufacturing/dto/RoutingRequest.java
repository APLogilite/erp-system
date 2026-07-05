package com.erp.modules.manufacturing.dto;

import java.util.List;

public class RoutingRequest {
  private String code;
  private String name;
  private String description;
  private List<RoutingOperationRequest> operations;

  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public List<RoutingOperationRequest> getOperations() { return operations; }
  public void setOperations(List<RoutingOperationRequest> operations) { this.operations = operations; }
}
