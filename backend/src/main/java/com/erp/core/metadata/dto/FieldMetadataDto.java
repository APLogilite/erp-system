package com.erp.core.metadata.dto;

import java.util.Map;

public class FieldMetadataDto {

  private String code;
  private String name;
  private String type;
  private boolean required;
  private boolean readonly;
  private boolean searchable;
  private boolean filterable;
  private String defaultValue;
  private Map<String, Object> properties;

  public FieldMetadataDto() {}

  public FieldMetadataDto(String code, String name, String type, boolean required, boolean readonly) {
    this.code = code;
    this.name = name;
    this.type = type;
    this.required = required;
    this.readonly = readonly;
    this.searchable = true;
    this.filterable = true;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public boolean isReadonly() {
    return readonly;
  }

  public void setReadonly(boolean readonly) {
    this.readonly = readonly;
  }

  public boolean isSearchable() {
    return searchable;
  }

  public void setSearchable(boolean searchable) {
    this.searchable = searchable;
  }

  public boolean isFilterable() {
    return filterable;
  }

  public void setFilterable(boolean filterable) {
    this.filterable = filterable;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }
}
