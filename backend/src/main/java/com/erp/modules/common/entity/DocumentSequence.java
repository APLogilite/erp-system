package com.erp.modules.common.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "sys_document_sequence")
public class DocumentSequence extends BaseEntity {

  @Column(name = "sequence_name", nullable = false, length = 100, unique = true)
  private String sequenceName;

  @Column(name = "prefix", nullable = false, length = 10)
  private String prefix;

  @Column(name = "next_value", nullable = false)
  private Integer nextValue = 1;

  @Column(name = "padding", nullable = false)
  private Integer padding = 4;

  @Column(name = "current_year")
  private Integer currentYear;

  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  // — getters / setters —

  public String getSequenceName() { return sequenceName; }
  public void setSequenceName(String sequenceName) { this.sequenceName = sequenceName; }

  public String getPrefix() { return prefix; }
  public void setPrefix(String prefix) { this.prefix = prefix; }

  public Integer getNextValue() { return nextValue; }
  public void setNextValue(Integer nextValue) { this.nextValue = nextValue; }

  public Integer getPadding() { return padding; }
  public void setPadding(Integer padding) { this.padding = padding; }

  public Integer getCurrentYear() { return currentYear; }
  public void setCurrentYear(Integer currentYear) { this.currentYear = currentYear; }

  public UUID getTenantId() { return tenantId; }
  public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
