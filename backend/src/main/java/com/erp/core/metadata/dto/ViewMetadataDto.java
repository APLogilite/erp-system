package com.erp.core.metadata.dto;

public class ViewMetadataDto {

  public enum ViewType {
    FORM, GRID, KANBAN, DETAIL
  }

  private String code;
  private String modelCode;
  private ViewType viewType;
  private String title;
  private LayoutMetadataDto layout;

  public ViewMetadataDto() {}

  public ViewMetadataDto(String code, String modelCode, ViewType viewType, String title) {
    this.code = code;
    this.modelCode = modelCode;
    this.viewType = viewType;
    this.title = title;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getModelCode() {
    return modelCode;
  }

  public void setModelCode(String modelCode) {
    this.modelCode = modelCode;
  }

  public ViewType getViewType() {
    return viewType;
  }

  public void setViewType(ViewType viewType) {
    this.viewType = viewType;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public LayoutMetadataDto getLayout() {
    return layout;
  }

  public void setLayout(LayoutMetadataDto layout) {
    this.layout = layout;
  }
}
