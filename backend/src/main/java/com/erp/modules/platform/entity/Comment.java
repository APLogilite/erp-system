package com.erp.modules.platform.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

  @Column(name = "module", nullable = false, length = 50)
  private String module;

  @Column(name = "record_id", nullable = false)
  private String recordId;

  @Column(name = "author", nullable = false)
  private String author;

  @Column(name = "body", nullable = false, columnDefinition = "text")
  private String body;

  @Column(name = "parent_id")
  private String parentId;

  @Column(name = "mentions", columnDefinition = "text")
  private String mentions;

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
}
