package com.erp.platform.identity.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class LoginResponse {

  private String accessToken;
  private String refreshToken;
  private LocalDateTime expiresAt;
  private UUID sessionId;
  private UserInfo user;

  public String getAccessToken() { return accessToken; }
  public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
  public String getRefreshToken() { return refreshToken; }
  public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
  public LocalDateTime getExpiresAt() { return expiresAt; }
  public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
  public UUID getSessionId() { return sessionId; }
  public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
  public UserInfo getUser() { return user; }
  public void setUser(UserInfo user) { this.user = user; }

  public static class UserInfo {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String displayName;
    private List<String> roles;
    private List<String> permissions;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
  }
}
