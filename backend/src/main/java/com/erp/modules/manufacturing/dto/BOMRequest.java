package com.erp.modules.manufacturing.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BOMRequest {
  private String code;
  private String name;
  private UUID productId;
  private String revision;
  private Integer version;
  private LocalDate effectiveFrom;
  private LocalDate effectiveTo;
  private String description;
  private List<BOMLineRequest> lines;

  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public UUID getProductId() { return productId; }
  public void setProductId(UUID productId) { this.productId = productId; }
  public String getRevision() { return revision; }
  public void setRevision(String revision) { this.revision = revision; }
  public Integer getVersion() { return version; }
  public void setVersion(Integer version) { this.version = version; }
  public LocalDate getEffectiveFrom() { return effectiveFrom; }
  public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }
  public LocalDate getEffectiveTo() { return effectiveTo; }
  public void setEffectiveTo(LocalDate effectiveTo) { this.effectiveTo = effectiveTo; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public List<BOMLineRequest> getLines() { return lines; }
  public void setLines(List<BOMLineRequest> lines) { this.lines = lines; }
}
