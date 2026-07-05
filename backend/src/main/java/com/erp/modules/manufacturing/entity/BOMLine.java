package com.erp.modules.manufacturing.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "bom_lines")
public class BOMLine extends BaseEntity {

  @Column(name = "bom_id", nullable = false)
  private UUID bomId;

  @Column(name = "line_no")
  private Integer lineNo;

  @Column(name = "component_id", nullable = false)
  private UUID componentId;

  @Column(nullable = false)
  private Double quantity = 1.0;

  @Column
  private String uom;

  @Column(name = "scrap_percentage")
  private Double scrapPercentage = 0.0;

  @Column(name = "operation_id")
  private UUID operationId;

  public UUID getBomId() { return bomId; }
  public void setBomId(UUID bomId) { this.bomId = bomId; }
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
