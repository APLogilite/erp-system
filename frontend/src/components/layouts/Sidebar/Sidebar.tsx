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
} from '@mui/material';
import { useNavigate, useLocation } from 'react-router-dom';

import { useAuthStore } from '@/core/auth/authStore';

const drawerWidth = 280;

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
];

type SidebarProps = { mobileOpen?: boolean; onMobileClose?: () => void };

export function Sidebar({ mobileOpen = false, onMobileClose }: SidebarProps) {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const navigate = useNavigate();
  const location = useLocation();
  const user = useAuthStore((s) => s.user);
  const isAdmin = user?.roles?.some((r) => r === 'sys_admin' || r === 'tnt_admin');

  const handleNavigate = (path: string) => {
    navigate(path);
    if (isMobile && onMobileClose) onMobileClose();
  };

  const drawer = (
    <Box sx={{ width: drawerWidth, pt: 2 }}>
      <Box sx={{ px: 2, py: 1 }}>
        <Typography variant="h6" fontWeight={700} color="primary">
          ERP System
        </Typography>
      </Box>
      <Divider sx={{ my: 1 }} />

      <ListSubheader sx={{ fontWeight: 600, fontSize: 11, lineHeight: '28px' }}>
        MODULES
      </ListSubheader>
      <List dense>
        {moduleItems.map((item) => (
          <ListItem key={item.text} disablePadding>
            <ListItemButton
              onClick={() => handleNavigate(item.path)}
              selected={location.pathname.startsWith(item.path)}
              sx={{ mx: 1, mb: 0.3, borderRadius: 1 }}
            >
              <ListItemIcon
                sx={{
                  minWidth: 36,
                  color: location.pathname.startsWith(item.path) ? 'primary.main' : undefined,
                }}
              >
                <item.icon fontSize="small" />
              </ListItemIcon>
              <ListItemText primary={item.text} primaryTypographyProps={{ fontSize: 14 }} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>

      <Divider sx={{ my: 1 }} />
      <ListSubheader sx={{ fontWeight: 600, fontSize: 11, lineHeight: '28px' }}>
        IDENTITY & ADMINISTRATION
      </ListSubheader>
      <List dense>
        {identityItems.map((item) => (
          <ListItem key={item.text} disablePadding>
            <ListItemButton
              onClick={() => handleNavigate(item.path)}
              selected={location.pathname === item.path}
              sx={{ mx: 1, mb: 0.3, borderRadius: 1 }}
            >
              <ListItemIcon sx={{ minWidth: 36 }}>
                <item.icon fontSize="small" />
              </ListItemIcon>
              <ListItemText primary={item.text} primaryTypographyProps={{ fontSize: 14 }} />
            </ListItemButton>
          </ListItem>
        ))}
        {isAdmin && <Divider sx={{ my: 0.5, mx: 2 }} />}
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
                sx={{ mx: 1, mb: 0.3, borderRadius: 1 }}
              >
                <ListItemIcon sx={{ minWidth: 36 }}>
                  {item.text === 'Overview' ? (
                    <AdminPanelSettings fontSize="small" color="primary" />
                  ) : (
                    <item.icon fontSize="small" />
                  )}
                </ListItemIcon>
                <ListItemText
                  primary={item.text}
                  primaryTypographyProps={{
                    fontSize: 14,
                    fontWeight: item.text === 'Overview' ? 600 : 400,
                  }}
                />
              </ListItemButton>
            </ListItem>
          ))}
      </List>
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
