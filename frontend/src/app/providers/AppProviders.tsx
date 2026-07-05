import { ReactNode } from 'react';
import { BrowserRouter } from 'react-router-dom';

import { ThemeProvider } from './ThemeProvider';

import { QueryProvider } from '@/core/query/QueryProvider';
import { RegistryProvider } from '@/core/registry';

type AppProvidersProps = {
  children: ReactNode;
};

export function AppProviders({ children }: AppProvidersProps) {
  return (
    <QueryProvider>
      <ThemeProvider>
        <RegistryProvider>
          <BrowserRouter>{children}</BrowserRouter>
        </RegistryProvider>
      </ThemeProvider>
    </QueryProvider>
  );
}
