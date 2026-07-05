package com.erp.platform.identity.security;

import io.jsonwebtoken.Claims;
import java.io.Serializable;
import java.util.UUID;

public class JwtPrincipal implements Serializable {

  private final UUID userId;
  private final String username;
  private final Claims claims;

  public JwtPrincipal(UUID userId, String username, Claims claims) {
    this.userId = userId;
    this.username = username;
    this.claims = claims;
  }

  public UUID getUserId() { return userId; }
  public String getUsername() { return username; }
  public Claims getClaims() { return claims; }
}
