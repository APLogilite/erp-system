import { ReactNode } from 'react';
import { Navigate, useLocation } from 'react-router-dom';

import { selectIsAuthenticated } from '@/core/auth/authSelectors';
import { useAuthStore } from '@/core/auth/authStore';

type GuestRouteProps = {
  children?: ReactNode;
};

export function GuestRoute({ children }: GuestRouteProps) {
  const isAuthenticated = useAuthStore(selectIsAuthenticated);
  const location = useLocation();

  if (isAuthenticated) {
    const state = location.state as { from?: { pathname?: string } } | null;
    const from = state?.from?.pathname || '/app/dashboard';
    return <Navigate to={from} replace />;
  }

  return children ? <>{children}</> : null;
}
