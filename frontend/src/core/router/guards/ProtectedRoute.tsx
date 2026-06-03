import { useState } from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';

import { AppLayout } from '@/components/layouts/AppLayout';
import { selectIsAuthenticated } from '@/core/auth/authSelectors';
import { useAuthStore } from '@/core/auth/authStore';

export function ProtectedRoute() {
  const isAuthenticated = useAuthStore(selectIsAuthenticated);
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  const handleDrawerToggle = () => {
    setMobileOpen((prev) => !prev);
  };

  return (
    <AppLayout mobileOpen={mobileOpen} onMobileClose={handleDrawerToggle}>
      <Outlet />
    </AppLayout>
  );
}
