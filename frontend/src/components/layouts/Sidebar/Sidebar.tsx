import {
  Dashboard,
  Dashboard as DashboardIcon,
  Inventory,
  ShoppingCart,
  People,
  Settings,
  Business,
  CorporateFare,
  Store,
  AccountTree,
  Group,
  VpnKey,
  Security,
  Devices,
  History,
  Person,
  AdminPanelSettings,
  TableChart,
  KeyboardArrowDown,
  KeyboardArrowRight,
} from '@mui/icons-material';
import {
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  useMediaQuery,
  useTheme,
  Box,
  Divider,
  Typography,
  ListSubheader,
  Collapse,
} from '@mui/material';
import { useCallback, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

import { useAuthStore } from '@/core/auth/authStore';
import { FormNavigationMenu } from '@/core/runtime/components/FormNavigationMenu';

export const SIDEBAR_WIDTH = 280;
const drawerWidth = SIDEBAR_WIDTH;

interface NavItem {
  text: string;
  icon: React.ElementType;
  path: string;
}

const moduleItems: NavItem[] = [
  { text: 'Dashboard', icon: Dashboard, path: '/app/dashboard' },
  { text: 'Products', icon: Inventory, path: '/app/products' },
  { text: 'Orders', icon: ShoppingCart, path: '/app/orders' },
];

const identityItems: NavItem[] = [
  { text: 'Profile', icon: Person, path: '/app/profile' },
  { text: 'Preferences', icon: Settings, path: '/app/preferences' },
  { text: 'Change Password', icon: VpnKey, path: '/app/change-password' },
  { text: 'Sessions', icon: Devices, path: '/app/sessions' },
];

const adminItems: NavItem[] = [
  { text: 'Overview', icon: DashboardIcon, path: '/app/admin' },
  { text: 'Tenants', icon: Business, path: '/app/admin/tenants' },
  { text: 'Organizations', icon: CorporateFare, path: '/app/admin/organizations' },
  { text: 'Companies', icon: Store, path: '/app/admin/companies' },
  { text: 'Branches', icon: AccountTree, path: '/app/admin/branches' },
  { text: 'Departments', icon: AccountTree, path: '/app/admin/departments' },
  { text: 'Users', icon: People, path: '/app/admin/users' },
  { text: 'Roles', icon: Group, path: '/app/admin/roles' },
  { text: 'Permissions', icon: Security, path: '/app/admin/permissions' },
  { text: 'Sessions', icon: Devices, path: '/app/admin/sessions' },
  { text: 'Audit Log', icon: History, path: '/app/admin/audit' },
  { text: 'Table Designer', icon: TableChart, path: '/app/admin/tables' },
  { text: 'Form Designer', icon: Dashboard, path: '/app/admin/forms' },
];

type SidebarProps = { mobileOpen?: boolean; onMobileClose?: () => void };

/** Collapsible group section in the sidebar */
function NavGroup({
  label,
  defaultOpen = true,
  children,
}: {
  label: string;
  defaultOpen?: boolean;
  children: React.ReactNode;
}) {
  const [open, setOpen] = useState(defaultOpen);
  const toggle = useCallback(() => setOpen((o) => !o), []);
  return (
    <>
      <ListSubheader
        sx={{
          fontWeight: 600,
          fontSize: 11,
          lineHeight: '28px',
          cursor: 'pointer',
          display: 'flex',
          alignItems: 'center',
          gap: 0.5,
          '&:hover': { color: 'primary.main' },
        }}
        onClick={toggle}
      >
        {open ? (
          <KeyboardArrowDown sx={{ fontSize: 16 }} />
        ) : (
          <KeyboardArrowRight sx={{ fontSize: 16 }} />
        )}
        {label}
      </ListSubheader>
      <Collapse in={open} timeout={200}>
        {children}
      </Collapse>
    </>
  );
}

export function Sidebar({ mobileOpen = false, onMobileClose }: SidebarProps) {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const navigate = useNavigate();
  const location = useLocation();
  const user = useAuthStore((s) => s.user);
  const isAdmin = user?.roles?.some((r) => r === 'sys_admin' || r === 'tnt_admin');

  const handleNavigate = useCallback(
    (path: string) => {
      navigate(path);
      if (isMobile && onMobileClose) onMobileClose();
    },
    [navigate, isMobile, onMobileClose]
  );

  const drawer = (
    <Box
      sx={{
        width: drawerWidth,
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
      }}
    >
      {/* Logo / Title */}
      <Box sx={{ px: 2, pt: 2, pb: 1 }}>
        <Typography variant="h6" fontWeight={700} color="primary">
          ERP System
        </Typography>
      </Box>
      <Divider />

      {/* Scrollable nav area */}
      <Box
        sx={{
          flex: 1,
          overflowY: 'auto',
          overflowX: 'hidden',
          pb: 2,
          // Smooth custom scrollbar
          '&::-webkit-scrollbar': { width: 4 },
          '&::-webkit-scrollbar-track': { background: 'transparent' },
          '&::-webkit-scrollbar-thumb': {
            background: theme.palette.divider,
            borderRadius: 2,
          },
          '&::-webkit-scrollbar-thumb:hover': {
            background: theme.palette.text.disabled,
          },
          scrollbarWidth: 'thin',
          scrollbarColor: `${theme.palette.divider} transparent`,
        }}
      >
        {/* MODULES group */}
        <NavGroup label="MODULES" defaultOpen>
          <List dense disablePadding>
            {moduleItems.map((item) => (
              <ListItem key={item.text} disablePadding>
                <ListItemButton
                  onClick={() => handleNavigate(item.path)}
                  selected={location.pathname.startsWith(item.path)}
                  sx={{ mx: 1, mb: 0.2, borderRadius: 1, py: 0.6 }}
                >
                  <ListItemIcon
                    sx={{
                      minWidth: 34,
                      color: location.pathname.startsWith(item.path) ? 'primary.main' : undefined,
                    }}
                  >
                    <item.icon fontSize="small" />
                  </ListItemIcon>
                  <ListItemText primary={item.text} primaryTypographyProps={{ fontSize: 13 }} />
                </ListItemButton>
              </ListItem>
            ))}
          </List>
        </NavGroup>

        <Divider sx={{ my: 0.5 }} />

        {/* DYNAMIC FORMS — rendered by FormNavigationMenu with its own collapse */}
        <FormNavigationMenu />

        <Divider sx={{ my: 0.5 }} />

        {/* IDENTITY & ADMINISTRATION group */}
        <NavGroup label="IDENTITY & ADMIN" defaultOpen={false}>
          <List dense disablePadding>
            {identityItems.map((item) => (
              <ListItem key={item.text} disablePadding>
                <ListItemButton
                  onClick={() => handleNavigate(item.path)}
                  selected={location.pathname === item.path}
                  sx={{ mx: 1, mb: 0.2, borderRadius: 1, py: 0.6 }}
                >
                  <ListItemIcon sx={{ minWidth: 34 }}>
                    <item.icon fontSize="small" />
                  </ListItemIcon>
                  <ListItemText primary={item.text} primaryTypographyProps={{ fontSize: 13 }} />
                </ListItemButton>
              </ListItem>
            ))}
            {isAdmin && <Divider sx={{ my: 0.3, mx: 2 }} />}
            {isAdmin &&
              adminItems.map((item) => (
                <ListItem key={item.text} disablePadding>
                  <ListItemButton
                    onClick={() => handleNavigate(item.path)}
                    selected={
                      item.path === '/app/admin'
                        ? location.pathname === '/app/admin'
                        : location.pathname.startsWith(item.path)
                    }
                    sx={{ mx: 1, mb: 0.2, borderRadius: 1, py: 0.6 }}
                  >
                    <ListItemIcon sx={{ minWidth: 34 }}>
                      {item.text === 'Overview' ? (
                        <AdminPanelSettings fontSize="small" color="primary" />
                      ) : (
                        <item.icon fontSize="small" />
                      )}
                    </ListItemIcon>
                    <ListItemText
                      primary={item.text}
                      primaryTypographyProps={{
                        fontSize: 13,
                        fontWeight: item.text === 'Overview' ? 600 : 400,
                      }}
                    />
                  </ListItemButton>
                </ListItem>
              ))}
          </List>
        </NavGroup>
      </Box>
    </Box>
  );

  if (isMobile) {
    return (
      <Drawer
        variant="temporary"
        open={mobileOpen}
        onClose={onMobileClose}
        ModalProps={{ keepMounted: true }}
        sx={{
          display: { xs: 'block', md: 'none' },
          '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
        }}
      >
        {drawer}
      </Drawer>
    );
  }

  return (
    <Drawer
      variant="permanent"
      sx={{
        display: { xs: 'none', md: 'block' },
        '& .MuiDrawer-paper': {
          boxSizing: 'border-box',
          width: drawerWidth,
          borderRight: `1px solid ${theme.palette.divider}`,
        },
      }}
      open
    >
      {drawer}
    </Drawer>
  );
}
