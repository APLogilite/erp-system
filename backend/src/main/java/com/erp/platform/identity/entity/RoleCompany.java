package com.erp.platform.identity.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "identity_role_companies", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"role_id", "company_id"})
})
public class RoleCompany extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  public Role getRole() { return role; }
  public void setRole(Role role) { this.role = role; }
  public Company getCompany() { return company; }
  public void setCompany(Company company) { this.company = company; }
}
