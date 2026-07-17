package com.erp.core.runtime.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.runtime.dto.window.WindowDefinitionResponse;
import com.erp.core.runtime.service.WindowDefinitionAssemblyService;
import com.erp.core.layout.entity.SysTable;
import com.erp.core.layout.entity.SysWindow;
import com.erp.core.layout.entity.SysWindowAccess;
import com.erp.core.layout.repository.SysTableRepository;
import com.erp.core.layout.repository.SysWindowAccessRepository;
import com.erp.core.layout.repository.SysWindowRepository;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import com.erp.platform.identity.entity.Role;
import com.erp.platform.identity.repository.RoleRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 * Runtime controller for window definitions.
 * Replaces the old /api/runtime/forms/{formCode}/definition endpoint.
 *
 * GET /api/v1/runtime/windows/{windowName}/definition
 * GET /api/v1/runtime/windows/accessible
 */
@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/runtime/windows")
public class WindowDefinitionController {

  private static final Logger log = LoggerFactory.getLogger(WindowDefinitionController.class);

  private final WindowDefinitionAssemblyService assemblyService;
  private final SysWindowRepository sysWindowRepository;
  private final SysWindowAccessRepository sysWindowAccessRepository;
  private final SysTableRepository sysTableRepository;
  private final RoleRepository roleRepository;

  public WindowDefinitionController(
      WindowDefinitionAssemblyService assemblyService,
      SysWindowRepository sysWindowRepository,
      SysWindowAccessRepository sysWindowAccessRepository,
      SysTableRepository sysTableRepository,
      RoleRepository roleRepository) {
    this.assemblyService = assemblyService;
    this.sysWindowRepository = sysWindowRepository;
    this.sysWindowAccessRepository = sysWindowAccessRepository;
    this.sysTableRepository = sysTableRepository;
    this.roleRepository = roleRepository;
  }

  /**
   * Returns the full window definition bundle for the given window name.
   * The response is cacheable (ETag + Cache-Control).
   *
   * @param windowName the window name (e.g. "sales_order")
   * @param request    the web request (for ETag support)
   * @return 200 with window bundle, 404 if not found, 401 if unauthenticated
   */
  @GetMapping("/{windowName}/definition")
  public ResponseEntity<ApiResponse<WindowDefinitionResponse>> getWindowDefinition(
      @PathVariable String windowName,
      WebRequest request) {

    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null || ctx.getTenantId() == null) {
      ApiResponse<WindowDefinitionResponse> errorResp = new ApiResponse<>(
          false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResp);
    }

    // Simple ETag based on window name + tenant
    String etag = "\"" + windowName + "-" + ctx.getTenantId() + "\"";

    // Check If-None-Match
    String ifNoneMatch = request.getHeader("If-None-Match");
    if (ifNoneMatch != null && ifNoneMatch.equals(etag)) {
      return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
          .eTag(etag)
          .build();
    }

    WindowDefinitionResponse bundle = assemblyService.assembleDefinition(windowName);

    if (bundle == null) {
      ApiResponse<WindowDefinitionResponse> notFoundResp = new ApiResponse<>(
          false, null, "Window not found: " + windowName, "NOT_FOUND", Collections.emptyList());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResp);
    }

    return ResponseEntity.ok()
        .eTag(etag)
        .header("Cache-Control", "max-age=300")
        .body(ApiResponse.success(bundle, "Window definition loaded."));
  }

  /**
   * Returns all windows the current user has role-based access to.
   * Replaces the old GET /runtime/forms endpoint which queried the PRD-001 metadata schema.
   * Results are lightweight (no tabs/fields) — suitable for search bars and window lists.
   *
   * GET /api/v1/runtime/windows/accessible
   */
  @GetMapping("/accessible")
  public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listAccessibleWindows() {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null || ctx.getTenantId() == null) {
      ApiResponse<List<Map<String, Object>>> errorResp = new ApiResponse<>(
          false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResp);
    }

    UUID tenantId = ctx.getTenantId();
    List<String> roleCodes = ctx.getRoles() != null ? ctx.getRoles() : List.of();
    boolean isSystemAdmin = roleCodes.contains("sys_admin");

    // Convert role codes to UUIDs
    List<Role> userRoles = roleRepository.findByCodeIn(roleCodes);
    Set<UUID> userRoleIds = userRoles.stream().map(Role::getId).collect(Collectors.toSet());

    // Load all active windows
    List<SysWindow> allWindows = sysWindowRepository.findAll().stream()
        .filter(w -> Boolean.TRUE.equals(w.getIsActive()))
        .toList();

    // Pre-load all SysTable entries for label resolution
    Map<UUID, SysTable> tableMap = sysTableRepository.findAll().stream()
        .collect(Collectors.toMap(SysTable::getId, t -> t));

    // Load all window access entries for the user's roles
    List<SysWindowAccess> accessEntries = sysWindowAccessRepository.findByRoleIdIn(
        new ArrayList<>(userRoleIds));

    // Build quick lookup: windowId → set of role IDs that have access
    Map<UUID, Set<UUID>> windowAccessMap = accessEntries.stream()
        .collect(Collectors.groupingBy(
            SysWindowAccess::getWindowId,
            Collectors.mapping(SysWindowAccess::getRoleId, Collectors.toSet())
        ));

    List<Map<String, Object>> accessibleWindows = new ArrayList<>();

    for (SysWindow window : allWindows) {
      boolean hasAccess = false;

      if (isSystemAdmin) {
        hasAccess = true;
      } else {
        // Check if any of user's roles have access to this window
        Set<UUID> allowedRoles = windowAccessMap.get(window.getId());
        if (allowedRoles != null) {
          hasAccess = userRoleIds.stream().anyMatch(allowedRoles::contains);
        }
      }

      if (!hasAccess) {
        continue;
      }

      // Resolve the associated table label
      SysTable table = tableMap.get(window.getTableId());

      Map<String, Object> entry = new LinkedHashMap<>();
      entry.put("windowId", window.getId().toString());
      entry.put("windowName", window.getName());
      entry.put("windowLabel", window.getDescription() != null ? window.getDescription() : window.getName());
      entry.put("tableName", table != null ? table.getName() : "");
      entry.put("tableLabel", table != null ? table.getLabel() : "");

      accessibleWindows.add(entry);
    }

    return ResponseEntity.ok(ApiResponse.success(accessibleWindows,
        "Accessible windows retrieved. Total: " + accessibleWindows.size()));
  }
}
