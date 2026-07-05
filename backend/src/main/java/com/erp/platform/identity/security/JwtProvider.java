package com.erp.platform.identity.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

  private final SecretKey secretKey;
  private final long accessTokenExpirationMs;
  private final long refreshTokenExpirationMs;

  public JwtProvider(
      @Value("${app.jwt.secret:CHANGE_ME_IN_PROD_CHANGE_ME_IN_PROD_32BYTES}") String secret,
      @Value("${app.jwt.access-token-expiration-ms:900000}") long accessTokenExpirationMs,
      @Value("${app.jwt.refresh-token-expiration-ms:604800000}") long refreshTokenExpirationMs) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessTokenExpirationMs = accessTokenExpirationMs;
    this.refreshTokenExpirationMs = refreshTokenExpirationMs;
  }

  public String generateAccessToken(UUID userId, String username, String email,
                                    UUID tenantId, String tenantCode,
                                    UUID organizationId, UUID companyId,
                                    UUID branchId, UUID sessionId,
                                    List<String> roles) {
    Instant now = Instant.now();
    Instant expiry = now.plusMillis(accessTokenExpirationMs);

    return Jwts.builder()
        .subject(userId.toString())
        .claim("username", username)
        .claim("email", email)
        .claim("tenantId", tenantId != null ? tenantId.toString() : null)
        .claim("tenantCode", tenantCode)
        .claim("organizationId", organizationId != null ? organizationId.toString() : null)
        .claim("companyId", companyId != null ? companyId.toString() : null)
        .claim("branchId", branchId != null ? branchId.toString() : null)
        .claim("sessionId", sessionId.toString())
        .claim("roles", roles)
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiry))
        .signWith(secretKey)
        .compact();
  }

  public String generateRefreshToken(UUID userId, UUID sessionId) {
    Instant now = Instant.now();
    Instant expiry = now.plusMillis(refreshTokenExpirationMs);

    return Jwts.builder()
        .subject(userId.toString())
        .claim("sessionId", sessionId.toString())
        .claim("type", "refresh")
        .issuedAt(Date.from(now))
        .expiration(Date.from(expiry))
        .signWith(secretKey)
        .compact();
  }

  public Claims validateToken(String token) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public boolean isValidToken(String token) {
    try {
      validateToken(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public LocalDateTime getExpiryFromToken(String token) {
    Claims claims = validateToken(token);
    return LocalDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault());
  }

  public String getSubjectFromToken(String token) {
    return validateToken(token).getSubject();
  }

  public long getAccessTokenExpirationMs() {
    return accessTokenExpirationMs;
  }

  public long getRefreshTokenExpirationMs() {
    return refreshTokenExpirationMs;
  }
}
