import { ReactNode } from 'react';
import { BrowserRouter } from 'react-router-dom';

import { ThemeProvider } from './ThemeProvider';

import { QueryProvider } from '@/core/query/QueryProvider';

type AppProvidersProps = {
  children: ReactNode;
};

export function AppProviders({ children }: AppProvidersProps) {
  return (
    <QueryProvider>
      <ThemeProvider>
        <BrowserRouter>{children}</BrowserRouter>
      </ThemeProvider>
    </QueryProvider>
  );
}
