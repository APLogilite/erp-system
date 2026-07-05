package com.erp.platform.identity.controller;

import com.erp.common.api.ApiResponse;
import com.erp.platform.identity.entity.UserSession;
import com.erp.platform.identity.service.SessionAdminService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/identity/sessions")
public class SessionAdminController {

  private final SessionAdminService sessionAdminService;
  public SessionAdminController(SessionAdminService sessionAdminService) { this.sessionAdminService = sessionAdminService; }

  @GetMapping public ResponseEntity<ApiResponse<List<UserSession>>> getActive() { return ResponseEntity.ok(ApiResponse.success(sessionAdminService.getActiveSessions(), "Active sessions")); }
  @GetMapping("/by-user") public ResponseEntity<ApiResponse<List<UserSession>>> byUser(@RequestParam UUID userId) { return ResponseEntity.ok(ApiResponse.success(sessionAdminService.getUserSessions(userId), "User sessions")); }
  @DeleteMapping("/{sessionId}") public ResponseEntity<ApiResponse<Void>> forceLogout(@PathVariable UUID sessionId) { sessionAdminService.forceLogout(sessionId); return ResponseEntity.ok(ApiResponse.successMessage("Session revoked")); }
  @DeleteMapping("/user/{userId}") public ResponseEntity<ApiResponse<Void>> forceLogoutAll(@PathVariable UUID userId) { sessionAdminService.forceLogoutAll(userId); return ResponseEntity.ok(ApiResponse.successMessage("All sessions revoked")); }
}
