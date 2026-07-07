package com.erp.platform.identity.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.Filter;

@Entity(name = "IdentityDepartment")
@Table(name = "identity_departments")
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Department extends BaseEntity {

  @Column(name = "code", nullable = false, unique = true, length = 50)
  private String code;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "cost_center", length = 50)
  private String costCenter;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "branch_id", nullable = false)
  private Branch branch;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Department parent;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tenant_id", nullable = false)
  private Tenant tenant;

  @Column(name = "level", nullable = false)
  private Integer level = 0;

  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getCostCenter() { return costCenter; }
  public void setCostCenter(String costCenter) { this.costCenter = costCenter; }
  public Branch getBranch() { return branch; }
  public void setBranch(Branch branch) { this.branch = branch; }
  public Department getParent() { return parent; }
  public void setParent(Department parent) { this.parent = parent; }
  public Integer getLevel() { return level; }
  public void setLevel(Integer level) { this.level = level; }
  public Tenant getTenant() { return tenant; }
  public void setTenant(Tenant tenant) { this.tenant = tenant; }
}
