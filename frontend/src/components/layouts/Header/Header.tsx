import { Menu as MenuIcon } from '@mui/icons-material';
import {
  AppBar,
  Toolbar,
  IconButton,
  Typography,
  Box,
  useTheme,
  useMediaQuery,
} from '@mui/material';

import { UserMenu } from './UserMenu';

import { FormSearchBar } from '@/core/runtime/components/FormSearchBar';
import { ContextSwitcher } from '@/modules/identity/context/ContextSwitcher';

type HeaderProps = {
  onMobileMenuToggle?: () => void;
  title?: string;
};

export function Header({ onMobileMenuToggle, title = 'ERP System' }: HeaderProps) {
  const theme = useTheme();
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
        <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1, fontWeight: 600 }}>
          {title}
        </Typography>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
          <FormSearchBar />
          <ContextSwitcher />
          <UserMenu />
        </Box>
      </Toolbar>
    </AppBar>
  );
}
