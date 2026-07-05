package com.erp.platform.identity.service;

import com.erp.platform.identity.entity.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordServiceTest {

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService();
    }

    @Test
    void shouldEncodeAndMatchPassword() {
        String raw = "Test@1234";
        String encoded = passwordService.encode(raw);
        assertThat(encoded).isNotEqualTo(raw);
        assertThat(passwordService.matches(raw, encoded)).isTrue();
        assertThat(passwordService.matches("wrong", encoded)).isFalse();
    }

    @Test
    void shouldRejectShortPassword() {
        assertThatThrownBy(() -> passwordService.validatePasswordPolicy("Ab1!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 8");
    }

    @Test
    void shouldRejectPasswordWithoutUppercase() {
        assertThatThrownBy(() -> passwordService.validatePasswordPolicy("abcdefgh1!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("uppercase");
    }

    @Test
    void shouldRejectPasswordWithoutLowercase() {
        assertThatThrownBy(() -> passwordService.validatePasswordPolicy("ABCDEFGH1!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("lowercase");
    }

    @Test
    void shouldRejectPasswordWithoutDigit() {
        assertThatThrownBy(() -> passwordService.validatePasswordPolicy("Abcdefgh!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("digit");
    }

    @Test
    void shouldRejectPasswordWithoutSpecialChar() {
        assertThatThrownBy(() -> passwordService.validatePasswordPolicy("Abcdefgh1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("special");
    }

    @Test
    void shouldAcceptValidPassword() {
        passwordService.validatePasswordPolicy("Str0ng!Pass");
    }

    @Test
    void shouldDetectLockedAccount() {
        UserAccount user = new UserAccount();
        user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
        assertThat(passwordService.isAccountLocked(user)).isTrue();
    }

    @Test
    void shouldDetectUnlockedAccount() {
        UserAccount user = new UserAccount();
        user.setLockedUntil(LocalDateTime.now().minusMinutes(30));
        assertThat(passwordService.isAccountLocked(user)).isFalse();
    }

    @Test
    void shouldDetectNullLockAsUnlocked() {
        UserAccount user = new UserAccount();
        assertThat(passwordService.isAccountLocked(user)).isFalse();
    }

    @Test
    void shouldTrackFailedAttempts() {
        UserAccount user = new UserAccount();
        passwordService.handleFailedAttempt(user);
        assertThat(user.getFailedAttempts()).isEqualTo(1);
        assertThat(user.getLockedUntil()).isNull();
    }

    @Test
    void shouldLockAfterFiveFailedAttempts() {
        UserAccount user = new UserAccount();
        for (int i = 0; i < 5; i++) {
            passwordService.handleFailedAttempt(user);
        }
        assertThat(user.getFailedAttempts()).isEqualTo(5);
        assertThat(user.getLockedUntil()).isNotNull();
        assertThat(user.getStatus()).isEqualTo("LOCKED");
    }

    @Test
    void shouldResetFailedAttempts() {
        UserAccount user = new UserAccount();
        user.setFailedAttempts(5);
        user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
        passwordService.resetFailedAttempts(user);
        assertThat(user.getFailedAttempts()).isZero();
        assertThat(user.getLockedUntil()).isNull();
    }
}
