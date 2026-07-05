package com.erp.platform.identity.authorization;

import com.erp.common.api.ApiResponse;
import com.erp.platform.identity.authorization.PermissionCache.PermissionEntry;
import com.erp.platform.identity.security.JwtPrincipal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthorizationController {

  private final AuthorizationService authorizationService;
  private final PermissionEvaluator permissionEvaluator;

  public AuthorizationController(AuthorizationService authorizationService,
                                 PermissionEvaluator permissionEvaluator) {
    this.authorizationService = authorizationService;
    this.permissionEvaluator = permissionEvaluator;
  }

  @GetMapping("/permissions")
  public ResponseEntity<ApiResponse<Map<String, Object>>> getPermissions(
      @AuthenticationPrincipal JwtPrincipal principal) {
    List<PermissionEntry> permissions = authorizationService.getEffectivePermissions();
    List<String> roles = authorizationService.getEffectiveRoles();

    Map<String, Object> result = Map.of(
        "userId", principal.getUserId().toString(),
        "username", principal.getUsername(),
        "roles", roles,
        "permissions", permissions.stream()
            .map(p -> p.getResourceType() + "." + p.getResource() + "." + p.getAction())
            .collect(Collectors.toList())
    );
    return ResponseEntity.ok(ApiResponse.success(result, "Effective permissions"));
  }

  @GetMapping("/check-permission")
  public ResponseEntity<ApiResponse<Boolean>> checkPermission(
      @AuthenticationPrincipal JwtPrincipal principal,
      @RequestParam String resourceType,
      @RequestParam String resource,
      @RequestParam String action) {
    boolean has = permissionEvaluator.hasPermission(
        principal.getUserId(), resourceType, resource, action);
    return ResponseEntity.ok(ApiResponse.success(has, "Permission check result"));
  }

  @PostMapping("/permissions/invalidate")
  public ResponseEntity<ApiResponse<Void>> invalidateCache(
      @AuthenticationPrincipal JwtPrincipal principal) {
    authorizationService.invalidateCacheForUser(principal.getUserId());
    return ResponseEntity.ok(ApiResponse.successMessage("Permission cache invalidated"));
  }
}
