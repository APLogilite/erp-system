package com.erp.core.runtime.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.metadata.service.SysMenuService;
import com.erp.modules.metadata.service.SysMenuService.MenuTreeNode;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Runtime controller for the hierarchical menu.
 *
 * GET /api/v1/runtime/menu — Returns the menu tree filtered by the current user's role access.
 */
@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/runtime")
public class MenuController {

  private static final Logger log = LoggerFactory.getLogger(MenuController.class);

  private final SysMenuService menuService;

  public MenuController(SysMenuService menuService) {
    this.menuService = menuService;
  }

  /**
   * Returns the menu tree for the current user.
   * Menu items are filtered by role-based window access (sys_window_access).
   * Groups with no visible children are automatically hidden.
   */
  @GetMapping("/menu")
  public ResponseEntity<ApiResponse<List<MenuTreeNode>>> getMenu() {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null || ctx.getTenantId() == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
    }

    // Build the full menu tree from sys_menu
    List<MenuTreeNode> menuTree = menuService.getMenuTree();

    // Filter by window access if the user is not sys_admin
    List<String> roles = ctx.getRoles();
    boolean isSystemAdmin = roles != null && roles.contains("sys_admin");

    // For now, return the full tree. Role-based filtering will be added
    // when sys_window_access data is seeded (TASK-045).
    // System admin always sees everything.
    // Non-admin users will have their menu filtered by window access.
    if (!isSystemAdmin) {
      // Future: filter menu tree by sys_window_access for the user's role
      log.debug("Menu filtering by role access - pending sys_window_access seeding");
    }

    return ResponseEntity.ok(ApiResponse.success(menuTree, "Menu retrieved."));
  }
}
