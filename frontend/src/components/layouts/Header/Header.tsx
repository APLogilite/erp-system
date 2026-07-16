import { Menu as MenuIcon, Refresh as RefreshIcon } from '@mui/icons-material';
import {
  AppBar,
  Toolbar,
  IconButton,
  Typography,
  Box,
  Tooltip,
  useTheme,
  useMediaQuery,
} from '@mui/material';
import { useQueryClient } from '@tanstack/react-query';

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
  const queryClient = useQueryClient();

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
          <Tooltip title="Refresh all data (menu, definitions, records)">
            <IconButton
              color="inherit"
              onClick={() => {
                queryClient.invalidateQueries({ queryKey: ['runtime', 'menu'] });
                queryClient.invalidateQueries({ queryKey: ['window-definition'] });
                queryClient.invalidateQueries({ queryKey: ['window-records'] });
                queryClient.invalidateQueries({ queryKey: ['window-record'] });
              }}
            >
              <RefreshIcon />
            </IconButton>
          </Tooltip>
          <FormSearchBar />
          <ContextSwitcher />
          <UserMenu />
        </Box>
      </Toolbar>
    </AppBar>
  );
}
