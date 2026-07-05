package com.erp.platform.identity.controller;

import com.erp.common.api.ApiResponse;
import com.erp.platform.identity.dto.ChangePasswordRequest;
import com.erp.platform.identity.dto.LoginRequest;
import com.erp.platform.identity.dto.LoginResponse;
import com.erp.platform.identity.dto.RefreshRequest;
import com.erp.platform.identity.dto.UserInfoResponse;
import com.erp.platform.identity.security.JwtPrincipal;
import com.erp.platform.identity.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("identityAuthController")
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthenticationService authenticationService;

  public AuthController(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponse>> login(
      @RequestBody LoginRequest request,
      HttpServletRequest httpRequest) {
    String ip = httpRequest.getRemoteAddr();
    String userAgent = httpRequest.getHeader("User-Agent");
    LoginResponse response = authenticationService.login(request, ip, userAgent);
    return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<LoginResponse>> refresh(
      @RequestBody RefreshRequest request,
      HttpServletRequest httpRequest) {
    String ip = httpRequest.getRemoteAddr();
    String userAgent = httpRequest.getHeader("User-Agent");
    LoginResponse response = authenticationService.refresh(request.getRefreshToken(), ip, userAgent);
    return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed"));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest httpRequest) {
    String authHeader = httpRequest.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      authenticationService.logout(token);
    }
    return ResponseEntity.ok(ApiResponse.successMessage("Logged out successfully"));
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserInfoResponse>> me(
      @AuthenticationPrincipal JwtPrincipal principal) {
    UserInfoResponse response = authenticationService.getCurrentUser(principal.getUserId());
    return ResponseEntity.ok(ApiResponse.success(response, "Current user retrieved"));
  }

  @PostMapping("/change-password")
  public ResponseEntity<ApiResponse<Void>> changePassword(
      @RequestBody ChangePasswordRequest request,
      @AuthenticationPrincipal JwtPrincipal principal) {
    authenticationService.changePassword(principal.getUserId(), request);
    return ResponseEntity.ok(ApiResponse.successMessage("Password changed successfully"));
  }
}
