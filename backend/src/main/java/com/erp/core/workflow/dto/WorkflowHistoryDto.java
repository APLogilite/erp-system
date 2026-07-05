package com.erp.core.workflow.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class WorkflowHistoryDto {

  private UUID id;
  private String modelCode;
  private UUID recordId;
  private String transitionCode;
  private String fromState;
  private String toState;
  private LocalDateTime occurredAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getModelCode() {
    return modelCode;
  }

  public void setModelCode(String modelCode) {
    this.modelCode = modelCode;
  }

  public UUID getRecordId() {
    return recordId;
  }

  public void setRecordId(UUID recordId) {
    this.recordId = recordId;
  }

  public String getTransitionCode() {
    return transitionCode;
  }

  public void setTransitionCode(String transitionCode) {
    this.transitionCode = transitionCode;
  }

  public String getFromState() {
    return fromState;
  }

  public void setFromState(String fromState) {
    this.fromState = fromState;
  }

  public String getToState() {
    return toState;
  }

  public void setToState(String toState) {
    this.toState = toState;
  }

  public LocalDateTime getOccurredAt() {
    return occurredAt;
  }

  public void setOccurredAt(LocalDateTime occurredAt) {
    this.occurredAt = occurredAt;
  }
}
