import { Menu as MenuIcon, Brightness4, Brightness7, Logout } from '@mui/icons-material';
import {
  AppBar,
  Toolbar,
  IconButton,
  Typography,
  Box,
  useTheme,
  useMediaQuery,
} from '@mui/material';

import { useTheme as useAppTheme } from '@/app/providers/ThemeProvider';
import { selectCurrentUser } from '@/core/auth/authSelectors';
import { useAuthStore } from '@/core/auth/authStore';

type HeaderProps = {
  onMobileMenuToggle?: () => void;
  title?: string;
};

export function Header({ onMobileMenuToggle, title = 'ERP System' }: HeaderProps) {
  const theme = useTheme();
  const { mode, toggleTheme } = useAppTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  const user = useAuthStore(selectCurrentUser);
  const logout = useAuthStore((state) => state.logout);

  return (
    <AppBar
      position="static"
      elevation={0}
      sx={{
        backgroundColor: theme.palette.background.paper,
        borderBottom: `1px solid ${theme.palette.divider}`,
        color: theme.palette.text.primary,
      }}
    >
      <Toolbar>
        {isMobile && (
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={onMobileMenuToggle}
            sx={{ mr: 2 }}
          >
            <MenuIcon />
          </IconButton>
        )}

        <Typography
          variant="h6"
          noWrap
          component="div"
          sx={{
            flexGrow: 1,
            fontWeight: 600,
            color: theme.palette.text.primary,
          }}
        >
          {title}
        </Typography>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
          {user && !isMobile && (
            <Typography
              variant="body2"
              sx={{ mr: 1, fontWeight: 600, color: theme.palette.text.secondary }}
            >
              {user.username} ({user.roles[0]})
            </Typography>
          )}
          <IconButton color="inherit" onClick={toggleTheme} aria-label="toggle theme">
            {mode === 'dark' ? <Brightness7 /> : <Brightness4 />}
          </IconButton>
          {user && (
            <IconButton color="error" onClick={logout} aria-label="logout" size="medium">
              <Logout fontSize="small" />
            </IconButton>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
}
