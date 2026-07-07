package com.erp.platform.identity.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "identity_role_branches", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"role_id", "branch_id"})
})
public class RoleBranch extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "branch_id", nullable = false)
  private Branch branch;

  public Role getRole() { return role; }
  public void setRole(Role role) { this.role = role; }
  public Branch getBranch() { return branch; }
  public void setBranch(Branch branch) { this.branch = branch; }
}
