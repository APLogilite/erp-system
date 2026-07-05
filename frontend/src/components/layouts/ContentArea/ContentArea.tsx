import { Box, Container } from '@mui/material';
import { ReactNode } from 'react';

type ContentAreaProps = {
  children: ReactNode;
  maxWidth?: 'xs' | 'sm' | 'md' | 'lg' | 'xl' | false;
  disablePadding?: boolean;
};

export function ContentArea({
  children,
  maxWidth = 'lg',
  disablePadding = false,
}: ContentAreaProps) {
  return (
    <Box
      component="main"
      sx={{
        flexGrow: 1,
        overflow: 'auto',
        backgroundColor: (theme) => theme.palette.background.default,
      }}
    >
      <Container
        maxWidth={maxWidth}
        sx={{
          py: disablePadding ? 0 : 3,
          px: disablePadding ? 0 : 3,
          minHeight: 'calc(100vh - 64px)', // Subtract header height
        }}
      >
        {children}
      </Container>
    </Box>
  );
}
