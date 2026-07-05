package com.erp.platform.identity.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private JwtProvider jwtProvider;
    private UUID userId;
    private UUID sessionId;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(
                "test-secret-key-that-is-long-enough-for-hmac-sha-256!!",
                900000L,
                604800000L
        );
        userId = UUID.randomUUID();
        sessionId = UUID.randomUUID();
    }

    @Test
    void shouldGenerateAndValidateAccessToken() {
        String token = jwtProvider.generateAccessToken(
                userId, "testuser", "test@example.com",
                null, null, null, null, null,
                sessionId, List.of("admin")
        );

        assertThat(token).isNotNull();
        assertThat(jwtProvider.isValidToken(token)).isTrue();

        Claims claims = jwtProvider.validateToken(token);
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("username", String.class)).isEqualTo("testuser");
        assertThat(claims.get("email", String.class)).isEqualTo("test@example.com");
        assertThat(claims.get("sessionId", String.class)).isEqualTo(sessionId.toString());
    }

    @Test
    void shouldGenerateAndValidateRefreshToken() {
        String token = jwtProvider.generateRefreshToken(userId, sessionId);
        assertThat(token).isNotNull();
        assertThat(jwtProvider.isValidToken(token)).isTrue();

        Claims claims = jwtProvider.validateToken(token);
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("type", String.class)).isEqualTo("refresh");
    }

    @Test
    void shouldRejectInvalidToken() {
        assertThat(jwtProvider.isValidToken("invalid-token")).isFalse();
    }

    @Test
    void shouldExtractSubject() {
        String token = jwtProvider.generateAccessToken(
                userId, "testuser", "test@example.com",
                null, null, null, null, null,
                sessionId, List.of()
        );
        assertThat(jwtProvider.getSubjectFromToken(token)).isEqualTo(userId.toString());
    }

    @Test
    void shouldGetExpirationFromToken() {
        String token = jwtProvider.generateAccessToken(
                userId, "testuser", "test@example.com",
                null, null, null, null, null,
                sessionId, List.of()
        );
        var expiry = jwtProvider.getExpiryFromToken(token);
        assertThat(expiry).isNotNull();
    }
}
