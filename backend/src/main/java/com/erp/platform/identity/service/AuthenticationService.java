package com.erp.platform.identity.service;

import com.erp.platform.identity.dto.ChangePasswordRequest;
import com.erp.platform.identity.dto.LoginRequest;
import com.erp.platform.identity.dto.LoginResponse;
import com.erp.platform.identity.dto.LoginResponse.UserInfo;
import com.erp.platform.identity.dto.UserInfoResponse;
import com.erp.platform.identity.authorization.PermissionCache.PermissionEntry;
import com.erp.platform.identity.authorization.PermissionResolver;
import com.erp.platform.identity.entity.*;
import com.erp.platform.identity.repository.*;
import com.erp.platform.identity.security.JwtProvider;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

  private final UserAccountRepository userRepository;
  private final UserSessionRepository sessionRepository;
  private final JwtProvider jwtProvider;
  private final PasswordService passwordService;
  private final UserRoleRepository userRoleRepository;
  private final UserOrganizationRepository userOrganizationRepository;
  private final PermissionResolver permissionResolver;

  public AuthenticationService(UserAccountRepository userRepository,
                               UserSessionRepository sessionRepository,
                               JwtProvider jwtProvider,
                               PasswordService passwordService,
                               UserRoleRepository userRoleRepository,
                               UserOrganizationRepository userOrganizationRepository,
                               PermissionResolver permissionResolver) {
    this.userRepository = userRepository;
    this.sessionRepository = sessionRepository;
    this.jwtProvider = jwtProvider;
    this.passwordService = passwordService;
    this.userRoleRepository = userRoleRepository;
    this.userOrganizationRepository = userOrganizationRepository;
    this.permissionResolver = permissionResolver;
  }

  @Transactional
  public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
    UserAccount user = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

    if (!user.getIsActive()) {
      throw new IllegalStateException("Invalid username or password");
    }

    if (passwordService.isAccountLocked(user)) {
      throw new IllegalStateException("Account is temporarily locked");
    }

    if (!"ACTIVE".equals(user.getStatus())) {
      if (user.getLockedUntil() != null && LocalDateTime.now().isAfter(user.getLockedUntil())) {
        passwordService.resetFailedAttempts(user);
        user.setStatus("ACTIVE");
        userRepository.save(user);
      } else {
        throw new IllegalStateException("Invalid username or password");
      }
    }

    if (!passwordService.matches(request.getPassword(), user.getPasswordHash())) {
      passwordService.handleFailedAttempt(user);
      userRepository.save(user);
      throw new IllegalArgumentException("Invalid username or password");
    }

    passwordService.resetFailedAttempts(user);
    user.setLastLoginAt(LocalDateTime.now());
    userRepository.save(user);

    // Resolve roles
    List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
    List<String> roleCodes = userRoles.stream()
        .map(ur -> ur.getRole().getCode())
        .collect(Collectors.toList());

    // Resolve permissions from roles
    List<PermissionEntry> permEntries = permissionResolver.resolveUserPermissions(user.getId());
    List<String> permissions = permEntries.stream()
        .map(e -> e.getResourceType() + ":" + e.getResource() + ":" + e.getAction())
        .collect(Collectors.toList());

    // Resolve tenant/org context
    List<UserOrganization> userOrgs = userOrganizationRepository.findByUserId(user.getId());
    UUID tenantId = null;
    UUID orgId = null;
    UUID companyId = null;
    if (!userOrgs.isEmpty()) {
      Organization org = userOrgs.get(0).getOrganization();
      tenantId = org.getTenant().getId();
      orgId = org.getId();
    }

    UserSession session = createSession(user, ipAddress, userAgent, tenantId, orgId, companyId);

    String accessToken = jwtProvider.generateAccessToken(
        user.getId(), user.getUsername(), user.getEmail(),
        tenantId, null, orgId, companyId, null,
        session.getId(), roleCodes);

    String refreshToken = jwtProvider.generateRefreshToken(user.getId(), session.getId());

    long expiresAt = jwtProvider.getAccessTokenExpirationMs();

    LoginResponse response = new LoginResponse();
    response.setAccessToken(accessToken);
    response.setRefreshToken(refreshToken);
    response.setExpiresAt(LocalDateTime.now().plusNanos(expiresAt * 1_000_000));
    response.setSessionId(session.getId());

    UserInfo userInfo = new UserInfo();
    userInfo.setId(user.getId());
    userInfo.setUsername(user.getUsername());
    userInfo.setEmail(user.getEmail());
    userInfo.setFirstName(user.getFirstName());
    userInfo.setLastName(user.getLastName());
    userInfo.setDisplayName(buildDisplayName(user));
    userInfo.setRoles(roleCodes);
    userInfo.setPermissions(permissions);
    response.setUser(userInfo);

    return response;
  }

  @Transactional
  public LoginResponse refresh(String refreshToken, String ipAddress, String userAgent) {
    if (!jwtProvider.isValidToken(refreshToken)) {
      throw new IllegalArgumentException("Invalid or expired refresh token");
    }

    String subject = jwtProvider.getSubjectFromToken(refreshToken);
    UUID userId = UUID.fromString(subject);

    UserAccount user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (!user.getIsActive() || !"ACTIVE".equals(user.getStatus())) {
      throw new IllegalStateException("Account is not active");
    }

    UserSession oldSession = sessionRepository.findById(
        UUID.fromString(jwtProvider.validateToken(refreshToken).get("sessionId", String.class)))
        .orElse(null);

    if (oldSession != null) {
      oldSession.softDelete();
      sessionRepository.save(oldSession);
    }

    List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
    List<String> roleCodes = userRoles.stream()
        .map(ur -> ur.getRole().getCode())
        .collect(Collectors.toList());

    List<PermissionEntry> permEntries = permissionResolver.resolveUserPermissions(user.getId());
    List<String> permissions = permEntries.stream()
        .map(e -> e.getResourceType() + ":" + e.getResource() + ":" + e.getAction())
        .collect(Collectors.toList());

    List<UserOrganization> userOrgs = userOrganizationRepository.findByUserId(user.getId());
    UUID tenantId = null;
    UUID orgId = null;
    UUID companyId = null;
    if (!userOrgs.isEmpty()) {
      Organization org = userOrgs.get(0).getOrganization();
      tenantId = org.getTenant().getId();
      orgId = org.getId();
    }

    UserSession session = createSession(user, ipAddress, userAgent, tenantId, orgId, companyId);

    String accessToken = jwtProvider.generateAccessToken(
        user.getId(), user.getUsername(), user.getEmail(),
        tenantId, null, orgId, companyId, null,
        session.getId(), roleCodes);

    String newRefreshToken = jwtProvider.generateRefreshToken(user.getId(), session.getId());

    LoginResponse response = new LoginResponse();
    response.setAccessToken(accessToken);
    response.setRefreshToken(newRefreshToken);
    response.setExpiresAt(LocalDateTime.now().plusNanos(jwtProvider.getAccessTokenExpirationMs() * 1_000_000));
    response.setSessionId(session.getId());

    UserInfo userInfo = new UserInfo();
    userInfo.setId(user.getId());
    userInfo.setUsername(user.getUsername());
    userInfo.setEmail(user.getEmail());
    userInfo.setFirstName(user.getFirstName());
    userInfo.setLastName(user.getLastName());
    userInfo.setDisplayName(buildDisplayName(user));
    userInfo.setRoles(roleCodes);
    userInfo.setPermissions(permissions);
    response.setUser(userInfo);

    return response;
  }

  @Transactional
  public void logout(String token) {
    try {
      if (!jwtProvider.isValidToken(token)) return;
      String sessionIdStr = jwtProvider.validateToken(token).get("sessionId", String.class);
      if (sessionIdStr != null) {
        UserSession session = sessionRepository.findById(UUID.fromString(sessionIdStr)).orElse(null);
        if (session != null) {
          session.softDelete();
          sessionRepository.save(session);
        }
      }
    } catch (Exception ignored) { }
  }

  @Transactional
  public void changePassword(UUID userId, ChangePasswordRequest request) {
    UserAccount user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (!passwordService.matches(request.getCurrentPassword(), user.getPasswordHash())) {
      throw new IllegalArgumentException("Current password is incorrect");
    }

    passwordService.validatePasswordPolicy(request.getNewPassword());

    user.setPasswordHash(passwordService.encode(request.getNewPassword()));
    user.setPasswordChangedAt(LocalDateTime.now());
    userRepository.save(user);
  }

  public UserInfoResponse getCurrentUser(UUID userId) {
    UserAccount user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    UserInfoResponse response = new UserInfoResponse();
    response.setId(user.getId());
    response.setUsername(user.getUsername());
    response.setEmail(user.getEmail());
    response.setFirstName(user.getFirstName());
    response.setLastName(user.getLastName());
    response.setDisplayName(buildDisplayName(user));
    response.setPhone(user.getPhone());
    response.setAvatarUrl(user.getAvatarUrl());
    response.setStatus(user.getStatus());
    response.setLastLoginAt(user.getLastLoginAt());
    return response;
  }

  private UserSession createSession(UserAccount user, String ipAddress, String userAgent,
                                     UUID tenantId, UUID organizationId, UUID companyId) {
    UserSession session = new UserSession();
    session.setUser(user);
    session.setToken(UUID.randomUUID().toString());
    session.setRefreshToken(UUID.randomUUID().toString());
    session.setIpAddress(ipAddress);
    session.setUserAgent(userAgent);
    session.setExpiresAt(LocalDateTime.now().plusDays(7));
    session.setLastActivityAt(LocalDateTime.now());
    session.setTenantId(tenantId);
    session.setOrganizationId(organizationId);
    session.setCompanyId(companyId);
    return sessionRepository.save(session);
  }

  private String buildDisplayName(UserAccount user) {
    if (user.getFirstName() != null && user.getLastName() != null) {
      return user.getFirstName() + " " + user.getLastName();
    }
    return user.getUsername();
  }
}
