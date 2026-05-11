import { Route, Routes } from 'react-router-dom';

import { DashboardPage } from '@/routes/dashboard/DashboardPage';
import { RouteLayout } from '@/routes/RouteLayout';

function HomePage() {
  return (
    <section>
      <h2>Welcome to Dynamic ERP</h2>
      <p>Foundation setup complete. This workspace is ready for enterprise module expansion.</p>
    </section>
  );
}

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<RouteLayout />}>
        <Route index element={<HomePage />} />
        <Route path="dashboard" element={<DashboardPage />} />
      </Route>
    </Routes>
  );
}
