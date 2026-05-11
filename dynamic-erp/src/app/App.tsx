import { ErrorBoundary } from '@/app/ErrorBoundary';
import { WelcomeCard } from '@/components/widgets/WelcomeCard';
import { getAppName } from '@/core/runtime/env';
import { AppRoutes } from '@/routes/AppRoutes';

export default function App() {
  return (
    <div className="app-shell">
      <header className="app-header">
        <h1>{getAppName()}</h1>
      </header>
      <ErrorBoundary>
        <main className="app-main">
          <WelcomeCard />
          <AppRoutes />
        </main>
      </ErrorBoundary>
    </div>
  );
}
