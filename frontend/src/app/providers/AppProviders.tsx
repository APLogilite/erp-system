import { ReactNode } from 'react';
import { BrowserRouter } from 'react-router-dom';

import { ThemeProvider } from './ThemeProvider';

type AppProvidersProps = {
  children: ReactNode;
};

export function AppProviders({ children }: AppProvidersProps) {
  return (
    <ThemeProvider>
      <BrowserRouter>{children}</BrowserRouter>
    </ThemeProvider>
  );
}
