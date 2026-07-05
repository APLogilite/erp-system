package com.erp.platform.identity.service;

import com.erp.platform.identity.entity.UserAccount;
import java.time.LocalDateTime;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

  private final PasswordEncoder passwordEncoder;

  public PasswordService() {
    this.passwordEncoder = new BCryptPasswordEncoder(12);
  }

  public String encode(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  public boolean matches(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  public void validatePasswordPolicy(String password) {
    if (password == null || password.length() < 8) {
      throw new IllegalArgumentException("Password must be at least 8 characters");
    }
    if (!password.matches(".*[A-Z].*")) {
      throw new IllegalArgumentException("Password must contain at least one uppercase letter");
    }
    if (!password.matches(".*[a-z].*")) {
      throw new IllegalArgumentException("Password must contain at least one lowercase letter");
    }
    if (!password.matches(".*\\d.*")) {
      throw new IllegalArgumentException("Password must contain at least one digit");
    }
    if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*")) {
      throw new IllegalArgumentException("Password must contain at least one special character");
    }
  }

  public boolean isAccountLocked(UserAccount user) {
    if (user.getLockedUntil() == null) return false;
    return LocalDateTime.now().isBefore(user.getLockedUntil());
  }

  public void handleFailedAttempt(UserAccount user) {
    int attempts = user.getFailedAttempts() == null ? 0 : user.getFailedAttempts();
    user.setFailedAttempts(attempts + 1);
    if (user.getFailedAttempts() >= 5) {
      user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
      user.setStatus("LOCKED");
    }
  }

  public void resetFailedAttempts(UserAccount user) {
    user.setFailedAttempts(0);
    user.setLockedUntil(null);
  }
}
