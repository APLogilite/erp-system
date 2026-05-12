import { Menu as MenuIcon, Brightness4, Brightness7 } from '@mui/icons-material';
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

type HeaderProps = {
  onMobileMenuToggle?: () => void;
  title?: string;
};

export function Header({ onMobileMenuToggle, title = 'ERP System' }: HeaderProps) {
  const theme = useTheme();
  const { mode, toggleTheme } = useAppTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

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

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <IconButton color="inherit" onClick={toggleTheme} aria-label="toggle theme">
            {mode === 'dark' ? <Brightness7 /> : <Brightness4 />}
          </IconButton>
        </Box>
      </Toolbar>
    </AppBar>
  );
}
