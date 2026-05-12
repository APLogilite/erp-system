import { ReactNode } from 'react';
import { Box } from '@mui/material';
import { Sidebar } from '../Sidebar';
import { Header } from '../Header';
import { ContentArea } from '../ContentArea';

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
        <ContentArea>
          {children}
        </ContentArea>
      </Box>
    </Box>
  );
}