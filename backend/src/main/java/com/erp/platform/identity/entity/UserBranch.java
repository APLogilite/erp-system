package com.erp.platform.identity.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "identity_user_branches", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "branch_id"})
})
public class UserBranch extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserAccount user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "branch_id", nullable = false)
  private Branch branch;

  @Column(name = "is_default")
  private Boolean isDefault = false;

  public UserAccount getUser() { return user; }
  public void setUser(UserAccount user) { this.user = user; }
  public Branch getBranch() { return branch; }
  public void setBranch(Branch branch) { this.branch = branch; }
  public Boolean getIsDefault() { return isDefault; }
  public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
}
