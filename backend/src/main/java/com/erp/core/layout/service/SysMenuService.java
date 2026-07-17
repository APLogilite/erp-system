package com.erp.core.layout.service;

import com.erp.common.base.BaseService;
import com.erp.core.layout.entity.SysMenu;
import com.erp.core.layout.entity.SysWindow;
import com.erp.core.layout.repository.SysMenuRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing hierarchical menu entries.
 * Includes tree-building logic for the frontend menu component.
 */
@Service
public class SysMenuService extends BaseService<SysMenu> {

  private final SysMenuRepository repository;
  private final SysWindowService windowService;

  public SysMenuService(SysMenuRepository repository, SysWindowService windowService) {
    this.repository = repository;
    this.windowService = windowService;
  }

  @Override
  protected JpaRepository<SysMenu, UUID> getRepository() {
    return repository;
  }

  /**
   * Returns the full menu tree starting from root-level entries.
   * Each entry includes its nested children (built as a flat list with parent-child links).
   */
  @Transactional(readOnly = true)
  public List<MenuTreeNode> getMenuTree() {
    List<SysMenu> allMenus = getRepository().findAll();
    Map<UUID, MenuTreeNode> nodeMap = new LinkedHashMap<>();
    List<MenuTreeNode> roots = new ArrayList<>();

    // Build all nodes
    for (SysMenu menu : allMenus) {
      nodeMap.put(menu.getId(), new MenuTreeNode(menu, resolveWindowName(menu)));
    }

    // Build tree structure
    for (SysMenu menu : allMenus) {
      MenuTreeNode node = nodeMap.get(menu.getId());
      UUID parentId = menu.getParentId();
      if (parentId != null && nodeMap.containsKey(parentId)) {
        nodeMap.get(parentId).addChild(node);
      } else {
        roots.add(node);
      }
    }

    // Sort roots by seqNo
    roots.sort((a, b) -> Integer.compare(a.getSeqNo(), b.getSeqNo()));

    return roots;
  }

  /**
   * Resolves the window name from a menu's windowId.
   */
  private String resolveWindowName(SysMenu menu) {
    if (menu.getWindowId() == null) {
      return null;
    }
    Optional<SysWindow> windowOpt = windowService.findById(menu.getWindowId());
    return windowOpt.map(SysWindow::getName).orElse(null);
  }

  /**
   * Returns root-level menu entries (parent_id IS NULL).
   */
  public List<SysMenu> getRootMenus() {
    return repository.findByParentIdIsNullOrderBySeqNoAsc();
  }

  /**
   * Returns children of a given menu entry.
   */
  public List<SysMenu> getChildren(UUID parentId) {
    return repository.findByParentIdOrderBySeqNoAsc(parentId);
  }

  @Override
  protected void beforeCreate(SysMenu entity) {
    if (entity.getName() == null || entity.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Menu name is required");
    }
    if (entity.getType() == null || (!"group".equals(entity.getType()) && !"window".equals(entity.getType()))) {
      throw new IllegalArgumentException("Menu type must be 'group' or 'window'");
    }
    if ("window".equals(entity.getType()) && entity.getWindowId() == null) {
      throw new IllegalArgumentException("Window menu items must have a window reference");
    }
  }

  /**
   * DTO for the menu tree structure returned to the frontend.
   */
  public static class MenuTreeNode {
    private UUID id;
    private String name;
    private String type;
    private UUID windowId;
    private String windowName;
    private String icon;
    private Integer seqNo;
    private List<MenuTreeNode> children = new ArrayList<>();

    public MenuTreeNode(SysMenu menu, String windowName) {
      this.id = menu.getId();
      this.name = menu.getName();
      this.type = menu.getType();
      this.windowId = menu.getWindowId();
      this.windowName = windowName;
      this.icon = menu.getIcon();
      this.seqNo = menu.getSeqNo();
    }

    public void addChild(MenuTreeNode child) {
      children.add(child);
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public UUID getWindowId() { return windowId; }
    public String getWindowName() { return windowName; }
    public String getIcon() { return icon; }
    public Integer getSeqNo() { return seqNo; }
    public List<MenuTreeNode> getChildren() { return children; }
  }
}
