package com.erp.core.metadata.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class VersionHistoryResponse {
  private UUID id;
  private Integer version;
  private UUID tableId;
  private String description;
  private Map<String, Object> definitionSnapshot;
  private UUID changedBy;
  private LocalDateTime createdAt;

  public VersionHistoryResponse() {}

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public Integer getVersion() { return version; }
  public void setVersion(Integer version) { this.version = version; }
  public UUID getTableId() { return tableId; }
  public void setTableId(UUID tableId) { this.tableId = tableId; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public Map<String, Object> getDefinitionSnapshot() { return definitionSnapshot; }
  public void setDefinitionSnapshot(Map<String, Object> definitionSnapshot) { this.definitionSnapshot = definitionSnapshot; }
  public UUID getChangedBy() { return changedBy; }
  public void setChangedBy(UUID changedBy) { this.changedBy = changedBy; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
