import { CssBaseline } from '@mui/material';
import { ThemeProvider as MuiThemeProvider } from '@mui/material/styles';
import { ReactNode, createContext, useContext, useState, useEffect } from 'react';

import { createAppTheme, ThemeMode } from '@/themes';

type ThemeContextType = {
  mode: ThemeMode;
  toggleTheme: () => void;
};

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export function useTheme() {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
}

type ThemeProviderProps = {
  children: ReactNode;
};

export function ThemeProvider({ children }: ThemeProviderProps) {
  const [mode, setMode] = useState<ThemeMode>(() => {
    // Initialize from localStorage or default to light
    const savedMode = localStorage.getItem('theme-mode') as ThemeMode;
    return savedMode || 'light';
  });

  const toggleTheme = () => {
    setMode((prevMode) => (prevMode === 'light' ? 'dark' : 'light'));
  };

  const theme = createAppTheme(mode);

  useEffect(() => {
    localStorage.setItem('theme-mode', mode);
  }, [mode]);

  const contextValue: ThemeContextType = {
    mode,
    toggleTheme,
  };

  return (
    <ThemeContext.Provider value={contextValue}>
      <MuiThemeProvider theme={theme}>
        <CssBaseline />
        {children}
      </MuiThemeProvider>
    </ThemeContext.Provider>
  );
}
