import { Box } from '@mui/material';
import { ReactNode } from 'react';

import { useDefinitionGeneration } from '@/core/runtime/hooks/useDefinitionGeneration';

import { ContentArea } from '../ContentArea';
import { Header } from '../Header';
import { Sidebar, SIDEBAR_WIDTH } from '../Sidebar';

type AppLayoutProps = {
  children: ReactNode;
  mobileOpen?: boolean;
  onMobileClose?: () => void;
};

export function AppLayout({ children, mobileOpen = false, onMobileClose }: AppLayoutProps) {
  useDefinitionGeneration(); // ENH-004: auto-invalidate caches on DB reseed

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh' }}>
      <Sidebar mobileOpen={mobileOpen} onMobileClose={onMobileClose} />
      <Box
        sx={{
          flexGrow: 1,
          display: 'flex',
          flexDirection: 'column',
          // Deskop: push content right by sidebar width to prevent overlap
          ml: { xs: 0, md: `${SIDEBAR_WIDTH}px` },
          width: { xs: '100%', md: `calc(100% - ${SIDEBAR_WIDTH}px)` },
        }}
      >
        <Header onMobileMenuToggle={onMobileClose} />
        <ContentArea>{children}</ContentArea>
      </Box>
    </Box>
  );
}
