package com.erp.modules.platform.dto;

import java.util.UUID;

public class SearchResultResponse {
  private UUID id;
  private String module;
  private String recordId;
  private String title;
  private String description;
  private String url;
  private float score;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getModule() { return module; }
  public void setModule(String module) { this.module = module; }
  public String getRecordId() { return recordId; }
  public void setRecordId(String recordId) { this.recordId = recordId; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }
  public float getScore() { return score; }
  public void setScore(float score) { this.score = score; }
}
