package com.erp.core.metadata.dto;

import java.util.List;
import java.util.Map;

public class LayoutMetadataDto {

  private String code;
  private String type;
  private Map<String, Object> config;
  private List<LayoutMetadataDto> children;

  public LayoutMetadataDto() {}

  public LayoutMetadataDto(String code, String type) {
    this.code = code;
    this.type = type;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Map<String, Object> getConfig() {
    return config;
  }

  public void setConfig(Map<String, Object> config) {
    this.config = config;
  }

  public List<LayoutMetadataDto> getChildren() {
    return children;
  }

  public void setChildren(List<LayoutMetadataDto> children) {
    this.children = children;
  }
}
