package com.erp.core.relation.dto;

import com.erp.core.relation.enums.LoadingStrategy;
import com.erp.core.relation.enums.RelationType;

public class RelationDefinitionDto {

  private String code;
  private RelationType relationType;
  private String targetModel;
  private String displayField;
  private String valueField;
  private boolean cascadeSave;
  private LoadingStrategy loadingStrategy;

  public RelationDefinitionDto() {}

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public RelationType getRelationType() {
    return relationType;
  }

  public void setRelationType(RelationType relationType) {
    this.relationType = relationType;
  }

  public String getTargetModel() {
    return targetModel;
  }

  public void setTargetModel(String targetModel) {
    this.targetModel = targetModel;
  }

  public String getDisplayField() {
    return displayField;
  }

  public void setDisplayField(String displayField) {
    this.displayField = displayField;
  }

  public String getValueField() {
    return valueField;
  }

  public void setValueField(String valueField) {
    this.valueField = valueField;
  }

  public boolean isCascadeSave() {
    return cascadeSave;
  }

  public void setCascadeSave(boolean cascadeSave) {
    this.cascadeSave = cascadeSave;
  }

  public LoadingStrategy getLoadingStrategy() {
    return loadingStrategy;
  }

  public void setLoadingStrategy(LoadingStrategy loadingStrategy) {
    this.loadingStrategy = loadingStrategy;
  }
}
