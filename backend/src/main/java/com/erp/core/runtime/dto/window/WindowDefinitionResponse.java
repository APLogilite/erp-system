package com.erp.core.runtime.dto.window;

import java.util.List;
import java.util.UUID;

/**
 * Full window definition bundle returned by GET /api/runtime/windows/{windowName}/definition.
 */
public class WindowDefinitionResponse {

  private WindowInfo window;
  private List<TabDefinitionResponse> tabs;

  public WindowDefinitionResponse() {}

  public WindowDefinitionResponse(WindowInfo window, List<TabDefinitionResponse> tabs) {
    this.window = window;
    this.tabs = tabs;
  }

  public WindowInfo getWindow() { return window; }
  public void setWindow(WindowInfo window) { this.window = window; }
  public List<TabDefinitionResponse> getTabs() { return tabs; }
  public void setTabs(List<TabDefinitionResponse> tabs) { this.tabs = tabs; }

  /**
   * Inner DTO for window-level metadata.
   */
  public static class WindowInfo {
    private UUID id;
    private String name;
    private UUID tableId;
    private String description;

    public WindowInfo() {}

    public WindowInfo(UUID id, String name, UUID tableId, String description) {
      this.id = id;
      this.name = name;
      this.tableId = tableId;
      this.description = description;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public UUID getTableId() { return tableId; }
    public void setTableId(UUID tableId) { this.tableId = tableId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
  }
}
