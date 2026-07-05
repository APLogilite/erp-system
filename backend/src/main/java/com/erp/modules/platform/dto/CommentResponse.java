package com.erp.modules.platform.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentResponse {
  private UUID id;
  private String module;
  private String recordId;
  private String author;
  private String body;
  private String parentId;
  private String mentions;
  private LocalDateTime createdAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getModule() { return module; }
  public void setModule(String module) { this.module = module; }
  public String getRecordId() { return recordId; }
  public void setRecordId(String recordId) { this.recordId = recordId; }
  public String getAuthor() { return author; }
  public void setAuthor(String author) { this.author = author; }
  public String getBody() { return body; }
  public void setBody(String body) { this.body = body; }
  public String getParentId() { return parentId; }
  public void setParentId(String parentId) { this.parentId = parentId; }
  public String getMentions() { return mentions; }
  public void setMentions(String mentions) { this.mentions = mentions; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
