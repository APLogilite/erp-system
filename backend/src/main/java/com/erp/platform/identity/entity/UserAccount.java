package com.erp.platform.identity.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "identity_users")
public class UserAccount extends BaseEntity {

  @Column(name = "username", nullable = false, unique = true, length = 100)
  private String username;

  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  @Column(name = "email", nullable = false, unique = true, length = 255)
  private String email;

  @Column(name = "first_name", length = 100)
  private String firstName;

  @Column(name = "last_name", length = 100)
  private String lastName;

  @Column(name = "phone", length = 30)
  private String phone;

  @Column(name = "avatar_url", length = 500)
  private String avatarUrl;

  @Column(name = "status", nullable = false, length = 20)
  private String status = "ACTIVE";

  @Column(name = "email_verified")
  private Boolean emailVerified = false;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  @Column(name = "failed_attempts")
  private Integer failedAttempts = 0;

  @Column(name = "locked_until")
  private LocalDateTime lockedUntil;

  @Column(name = "password_changed_at")
  private LocalDateTime passwordChangedAt;

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }
  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public String getAvatarUrl() { return avatarUrl; }
  public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Boolean getEmailVerified() { return emailVerified; }
  public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
  public LocalDateTime getLastLoginAt() { return lastLoginAt; }
  public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
  public Integer getFailedAttempts() { return failedAttempts; }
  public void setFailedAttempts(Integer failedAttempts) { this.failedAttempts = failedAttempts; }
  public LocalDateTime getLockedUntil() { return lockedUntil; }
  public void setLockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }
  public LocalDateTime getPasswordChangedAt() { return passwordChangedAt; }
  public void setPasswordChangedAt(LocalDateTime passwordChangedAt) { this.passwordChangedAt = passwordChangedAt; }
}
