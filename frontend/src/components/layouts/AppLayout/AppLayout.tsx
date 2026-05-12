import { Box } from '@mui/material';
import { ReactNode } from 'react';

import { ContentArea } from '../ContentArea';
import { Header } from '../Header';
import { Sidebar } from '../Sidebar';

type AppLayoutProps = {
  children: ReactNode;
  mobileOpen?: boolean;
  onMobileClose?: () => void;
};

export function AppLayout({ children, mobileOpen = false, onMobileClose }: AppLayoutProps) {
  return (
    <Box sx={{ display: 'flex', minHeight: '100vh' }}>
      <Sidebar mobileOpen={mobileOpen} onMobileClose={onMobileClose} />
      <Box sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
        <Header onMobileMenuToggle={onMobileClose} />
        <ContentArea>{children}</ContentArea>
      </Box>
    </Box>
  );
}
