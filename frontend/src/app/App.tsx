import { useState } from 'react';

import { ErrorBoundary } from '@/app/ErrorBoundary';
import { AppLayout } from '@/components/layouts/AppLayout';
import { AppRoutes } from '@/routes/AppRoutes';

export default function App() {
  const [mobileOpen, setMobileOpen] = useState(false);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  return (
    <ErrorBoundary>
      <AppLayout mobileOpen={mobileOpen} onMobileClose={handleDrawerToggle}>
        <AppRoutes />
      </AppLayout>
    </ErrorBoundary>
  );
}
