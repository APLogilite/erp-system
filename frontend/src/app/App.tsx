import { ErrorBoundary } from '@/app/ErrorBoundary';
import { AppRoutes } from '@/routes/AppRoutes';

export default function App() {
  return (
    <ErrorBoundary>
      <AppRoutes />
    </ErrorBoundary>
  );
}
