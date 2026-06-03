package com.erp.core.metadata.dto;

import java.util.Map;

public class ActionMetadataDto {

  public enum ActionType {
    CREATE, UPDATE, DELETE, SUBMIT, APPROVE, REJECT, EXPORT, CUSTOM
  }

  private String code;
  private String name;
  private ActionType actionType;
  private Map<String, Object> config;

  public ActionMetadataDto() {}

  public ActionMetadataDto(String code, String name, ActionType actionType) {
    this.code = code;
    this.name = name;
    this.actionType = actionType;
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

  public ActionType getActionType() {
    return actionType;
  }

  public void setActionType(ActionType actionType) {
    this.actionType = actionType;
  }

  public Map<String, Object> getConfig() {
    return config;
  }

  public void setConfig(Map<String, Object> config) {
    this.config = config;
  }
}
