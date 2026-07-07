package com.erp.platform.identity.service;

import com.erp.platform.identity.entity.Role;
import com.erp.platform.identity.entity.UserAccount;
import com.erp.platform.identity.entity.UserPreference;
import com.erp.platform.identity.entity.UserRole;
import com.erp.platform.identity.repository.RoleRepository;
import com.erp.platform.identity.repository.UserAccountRepository;
import com.erp.platform.identity.repository.UserPreferenceRepository;
import com.erp.platform.identity.repository.UserRoleRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAdminService {

  private final UserAccountRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final UserPreferenceRepository userPreferenceRepository;
  private final RoleRepository roleRepository;
  private final PasswordService passwordService;

  public UserAdminService(UserAccountRepository userRepository,
                          UserRoleRepository userRoleRepository,
                          UserPreferenceRepository userPreferenceRepository,
                          RoleRepository roleRepository,
                          PasswordService passwordService) {
    this.userRepository = userRepository;
    this.userRoleRepository = userRoleRepository;
    this.userPreferenceRepository = userPreferenceRepository;
    this.roleRepository = roleRepository;
    this.passwordService = passwordService;
  }

  @Transactional(readOnly = true)
  public List<UserAccount> getAllUsers() {
    return userRepository.findAll();
  }

  public UserAccount getUser(UUID id) {
    return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
  }

  @Transactional
  public UserAccount createUser(UserAccount user) {
    if (userRepository.existsByUsername(user.getUsername())) {
      throw new IllegalArgumentException("Username already exists");
    }
    if (userRepository.existsByEmail(user.getEmail())) {
      throw new IllegalArgumentException("Email already exists");
    }
    passwordService.validatePasswordPolicy(user.getPasswordHash());
    user.setPasswordHash(passwordService.encode(user.getPasswordHash()));
    if (user.getStatus() == null) user.setStatus("PENDING");
    if (user.getEmailVerified() == null) user.setEmailVerified(false);
    UserAccount saved = userRepository.save(user);

    UserPreference prefs = new UserPreference();
    prefs.setUser(saved);
    userPreferenceRepository.save(prefs);
    return saved;
  }

  @Transactional
  public UserAccount updateUser(UUID id, UserAccount req) {
    UserAccount u = getUser(id);
    u.setFirstName(req.getFirstName());
    u.setLastName(req.getLastName());
    u.setEmail(req.getEmail());
    u.setPhone(req.getPhone());
    u.setAvatarUrl(req.getAvatarUrl());
    u.setBirthDate(req.getBirthDate());
    u.setWebsite(req.getWebsite());
    u.setEmployeeId(req.getEmployeeId());
    u.setAddress(req.getAddress());
    if (req.getEmailVerified() != null) u.setEmailVerified(req.getEmailVerified());
    if (req.getStatus() != null) u.setStatus(req.getStatus());
    return userRepository.save(u);
  }

  @Transactional
  public void deactivateUser(UUID id) {
    UserAccount u = getUser(id);
    u.setIsActive(false);
    u.setStatus("INACTIVE");
    userRepository.save(u);
  }

  @Transactional
  public void activateUser(UUID id) {
    UserAccount u = getUser(id);
    u.setIsActive(true);
    u.setStatus("ACTIVE");
    userRepository.save(u);
  }

  @Transactional
  public void resetPassword(UUID id, String newPassword) {
    UserAccount u = getUser(id);
    passwordService.validatePasswordPolicy(newPassword);
    u.setPasswordHash(passwordService.encode(newPassword));
    u.setPasswordChangedAt(LocalDateTime.now());
    u.setFailedAttempts(0);
    u.setLockedUntil(null);
    u.setStatus("ACTIVE");
    userRepository.save(u);
  }

  @Transactional
  public void unlockUser(UUID id) {
    UserAccount u = getUser(id);
    u.setFailedAttempts(0);
    u.setLockedUntil(null);
    u.setStatus("ACTIVE");
    userRepository.save(u);
  }

  @Transactional
  public void assignRole(UUID userId, UUID roleId) {
    UserAccount u = getUser(userId);
    Role r = roleRepository.findById(roleId).orElseThrow(() -> new IllegalArgumentException("Role not found"));
    UserRole ur = new UserRole();
    ur.setUser(u);
    ur.setRole(r);
    userRoleRepository.save(ur);
  }

  @Transactional
  public void removeRole(UUID userId, UUID roleId) {
    userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
  }

  @Transactional
  public void updatePreferences(UUID userId, String language, String timezone,
                                String dateFormat, String timeFormat,
                                String numberFormat, String currency, String theme) {
    UserPreference prefs = userPreferenceRepository.findByUserId(userId)
        .orElseGet(() -> {
          UserPreference p = new UserPreference();
          p.setUser(getUser(userId));
          return p;
        });
    if (language != null) prefs.setLanguage(language);
    if (timezone != null) prefs.setTimezone(timezone);
    if (dateFormat != null) prefs.setDateFormat(dateFormat);
    if (timeFormat != null) prefs.setTimeFormat(timeFormat);
    if (numberFormat != null) prefs.setNumberFormat(numberFormat);
    if (currency != null) prefs.setCurrency(currency);
    if (theme != null) prefs.setTheme(theme);
    userPreferenceRepository.save(prefs);
  }

  public UserPreference getUserPreferences(UUID userId) {
    return userPreferenceRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("Preferences not found"));
  }

  public List<UserRole> getUserRoles(UUID userId) { return userRoleRepository.findByUserId(userId); }
}
