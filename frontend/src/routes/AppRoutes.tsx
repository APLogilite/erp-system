import { Route, Routes } from 'react-router-dom';

import { DashboardPage } from '@/routes/dashboard/DashboardPage';

function HomePage() {
  return (
    <div>
      <h2>Welcome to Dynamic ERP</h2>
      <p>Foundation setup complete. This workspace is ready for enterprise module expansion.</p>
    </div>
  );
}

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/app/dashboard" element={<DashboardPage />} />
      <Route path="/app/products" element={<div>Products Page - Coming Soon</div>} />
      <Route path="/app/orders" element={<div>Orders Page - Coming Soon</div>} />
      <Route path="/app/users" element={<div>Users Page - Coming Soon</div>} />
      <Route path="/app/settings" element={<div>Settings Page - Coming Soon</div>} />
    </Routes>
  );
}
