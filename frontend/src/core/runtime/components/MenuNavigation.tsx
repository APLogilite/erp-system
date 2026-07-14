import {
  Collapse,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  ListSubheader,
} from '@mui/material';
import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

import { useMenuItems } from '../hooks/useMenuItems';
import type { MenuTreeNode } from '../api/runtimeApi';

// Simple folder/file icons using unicode/MUI
const GROUP_EXPANDED = '▼';
const GROUP_COLLAPSED = '▶';
const WINDOW_ICON = '📄';

interface MenuGroupProps {
  node: MenuTreeNode;
  depth?: number;
  onNavigate: (windowName: string | undefined) => void;
  currentPath: string;
}

/** Recursively renders a menu group or window item. */
function MenuGroupItem({ node, depth = 0, onNavigate, currentPath }: MenuGroupProps) {
  const [isOpen, setIsOpen] = useState(depth === 0); // Open root groups by default

  if (node.type === 'window') {
    // Window leaf item
    const isSelected = node.windowName ? currentPath === `/app/window/${node.windowName}` : false;
    return (
      <ListItem disablePadding>
        <ListItemButton
          onClick={() => onNavigate(node.windowName)}
          selected={isSelected}
          sx={{ mx: 1, mb: 0.3, borderRadius: 1, pl: 1 + depth * 2 }}
        >
          <ListItemIcon sx={{ minWidth: 30 }}>
            <span style={{ fontSize: 16 }}>{WINDOW_ICON}</span>
          </ListItemIcon>
          <ListItemText primary={node.name} primaryTypographyProps={{ fontSize: 13 }} />
        </ListItemButton>
      </ListItem>
    );
  }

  // Group item (collapsible)
  const hasVisibleChildren = node.children && node.children.length > 0;
  if (!hasVisibleChildren) return null;

  return (
    <>
      <ListItem disablePadding>
        <ListItemButton
          onClick={() => setIsOpen(!isOpen)}
          sx={{ mx: 1, mb: 0.3, borderRadius: 1, pl: depth === 0 ? 1 : 1 + depth * 2 }}
        >
          <ListItemIcon sx={{ minWidth: 20 }}>
            <span style={{ fontSize: 12, fontWeight: 600 }}>
              {isOpen ? GROUP_EXPANDED : GROUP_COLLAPSED}
            </span>
          </ListItemIcon>
          <ListItemText
            primary={node.name}
            primaryTypographyProps={{ fontSize: 13, fontWeight: depth === 0 ? 600 : 500 }}
          />
        </ListItemButton>
      </ListItem>
      <Collapse in={isOpen} timeout="auto" unmountOnExit>
        <List dense disablePadding>
          {node.children.map((child: MenuTreeNode) => (
            <MenuGroupItem
              key={child.id}
              node={child}
              depth={depth + 1}
              onNavigate={onNavigate}
              currentPath={currentPath}
            />
          ))}
        </List>
      </Collapse>
    </>
  );
}

/**
 * Hierarchical menu navigation component.
 * Replaces the old flat FormNavigationMenu with a tree-based menu that
 * supports collapsible groups and nested window items.
 */
export function MenuNavigation() {
  const { data: menuTree, isLoading } = useMenuItems();
  const navigate = useNavigate();
  const location = useLocation();

  if (isLoading) return null;
  if (!menuTree || menuTree.length === 0) return null;

  const handleNavigate = (windowName: string | undefined) => {
    if (!windowName) return;
    navigate(`/app/window/${encodeURIComponent(windowName)}`);
  };

  return (
    <>
      <ListSubheader sx={{ fontWeight: 600, fontSize: 11, lineHeight: '28px' }}>MENU</ListSubheader>
      <List dense>
        {menuTree.map((node) => (
          <MenuGroupItem
            key={node.id}
            node={node}
            depth={0}
            onNavigate={handleNavigate}
            currentPath={location.pathname + location.search}
          />
        ))}
      </List>
    </>
  );
}
