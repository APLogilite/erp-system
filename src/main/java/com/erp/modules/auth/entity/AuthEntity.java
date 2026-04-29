package com.erp.modules.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * Auth entity placeholder.
 * Kept minimal; no business fields/constraints yet.
 */
@Entity
@Table(name = "auth_entities")
public class AuthEntity {

  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
  private UUID id;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }
}
