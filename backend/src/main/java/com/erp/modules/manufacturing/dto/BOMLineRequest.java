package com.erp.modules.manufacturing.dto;

import java.util.UUID;

public class BOMLineRequest {
  private Integer lineNo;
  private UUID componentId;
  private Double quantity;
  private String uom;
  private Double scrapPercentage;
  private UUID operationId;

  public Integer getLineNo() { return lineNo; }
  public void setLineNo(Integer lineNo) { this.lineNo = lineNo; }
  public UUID getComponentId() { return componentId; }
  public void setComponentId(UUID componentId) { this.componentId = componentId; }
  public Double getQuantity() { return quantity; }
  public void setQuantity(Double quantity) { this.quantity = quantity; }
  public String getUom() { return uom; }
  public void setUom(String uom) { this.uom = uom; }
  public Double getScrapPercentage() { return scrapPercentage; }
  public void setScrapPercentage(Double scrapPercentage) { this.scrapPercentage = scrapPercentage; }
  public UUID getOperationId() { return operationId; }
  public void setOperationId(UUID operationId) { this.operationId = operationId; }
}
