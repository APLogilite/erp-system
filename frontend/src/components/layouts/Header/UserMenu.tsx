import {
  Person,
  VpnKey,
  Logout,
  Devices,
  Palette,
  Brightness4,
  Brightness7,
} from '@mui/icons-material';
import {
  Avatar,
  Box,
  IconButton,
  Menu,
  MenuItem,
  ListItemIcon,
  ListItemText,
  Divider,
  Typography,
} from '@mui/material';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { useTheme as useAppTheme } from '@/app/providers/ThemeProvider';
import { selectCurrentUser } from '@/core/auth/authSelectors';
import { useAuthStore } from '@/core/auth/authStore';

export function UserMenu() {
  const user = useAuthStore(selectCurrentUser);
  const logout = useAuthStore((s) => s.logout);
  const navigate = useNavigate();
  const { mode, toggleTheme } = useAppTheme();
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
  const displayName = user?.displayName || user?.username;

  if (!user) return null;

  return (
    <Box>
      <IconButton onClick={(e) => setAnchorEl(e.currentTarget)} size="small" sx={{ ml: 1 }}>
        <Avatar sx={{ width: 32, height: 32, bgcolor: 'primary.main', fontSize: 14 }}>
          {displayName?.charAt(0).toUpperCase()}
        </Avatar>
      </IconButton>

      <Menu
        anchorEl={anchorEl}
        open={!!anchorEl}
        onClose={() => setAnchorEl(null)}
        transformOrigin={{ horizontal: 'right', vertical: 'top' }}
        anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
        PaperProps={{ sx: { width: 220, mt: 1 } }}
      >
        <Box sx={{ px: 2, py: 1 }}>
          <Typography variant="subtitle2" fontWeight={600}>
            {displayName}
          </Typography>
          <Typography variant="caption" color="text.secondary">
            {user.email}
          </Typography>
        </Box>
        <Divider />

        <MenuItem
          onClick={() => {
            navigate('/app/profile');
            setAnchorEl(null);
          }}
        >
          <ListItemIcon>
            <Person fontSize="small" />
          </ListItemIcon>
          <ListItemText>Profile</ListItemText>
        </MenuItem>
        <MenuItem
          onClick={() => {
            navigate('/app/preferences');
            setAnchorEl(null);
          }}
        >
          <ListItemIcon>
            <Palette fontSize="small" />
          </ListItemIcon>
          <ListItemText>Preferences</ListItemText>
        </MenuItem>
        <MenuItem onClick={toggleTheme}>
          <ListItemIcon>
            {mode === 'dark' ? <Brightness7 fontSize="small" /> : <Brightness4 fontSize="small" />}
          </ListItemIcon>
          <ListItemText>{mode === 'dark' ? 'Light Mode' : 'Dark Mode'}</ListItemText>
        </MenuItem>
        <MenuItem
          onClick={() => {
            navigate('/app/change-password');
            setAnchorEl(null);
          }}
        >
          <ListItemIcon>
            <VpnKey fontSize="small" />
          </ListItemIcon>
          <ListItemText>Change Password</ListItemText>
        </MenuItem>
        <MenuItem
          onClick={() => {
            navigate('/app/sessions');
            setAnchorEl(null);
          }}
        >
          <ListItemIcon>
            <Devices fontSize="small" />
          </ListItemIcon>
          <ListItemText>Sessions</ListItemText>
        </MenuItem>

        <Divider />
        <MenuItem onClick={logout} sx={{ color: 'error.main' }}>
          <ListItemIcon>
            <Logout fontSize="small" color="error" />
          </ListItemIcon>
          <ListItemText>Logout</ListItemText>
        </MenuItem>
      </Menu>
    </Box>
  );
}
