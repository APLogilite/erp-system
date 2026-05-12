import { ReactNode } from 'react';
import {
  Box,
  Typography,
  Breadcrumbs,
  Link as MuiLink,
  Paper,
} from '@mui/material';
import { Link, useLocation } from 'react-router-dom';

type BreadcrumbItem = {
  label: string;
  path?: string;
};

type PageContainerProps = {
  title: string;
  subtitle?: string;
  breadcrumbs?: BreadcrumbItem[];
  children: ReactNode;
  actions?: ReactNode;
};

export function PageContainer({
  title,
  subtitle,
  breadcrumbs,
  children,
  actions,
}: PageContainerProps) {
  const location = useLocation();

  const generateBreadcrumbs = (): BreadcrumbItem[] => {
    if (breadcrumbs) return breadcrumbs;

    // Auto-generate breadcrumbs from current path
    const pathSegments = location.pathname.split('/').filter(Boolean);
    const crumbs: BreadcrumbItem[] = [{ label: 'Home', path: '/app/dashboard' }];

    let currentPath = '';
    pathSegments.forEach((segment, index) => {
      currentPath += `/${segment}`;
      if (index < pathSegments.length - 1) {
        crumbs.push({
          label: segment.charAt(0).toUpperCase() + segment.slice(1),
          path: currentPath,
        });
      } else {
        crumbs.push({
          label: segment.charAt(0).toUpperCase() + segment.slice(1),
        });
      }
    });

    return crumbs;
  };

  const breadcrumbItems = generateBreadcrumbs();

  return (
    <Box>
      {/* Header Section */}
      <Box sx={{ mb: 3 }}>
        {breadcrumbItems.length > 1 && (
          <Breadcrumbs sx={{ mb: 2 }}>
            {breadcrumbItems.map((item, index) => {
              const isLast = index === breadcrumbItems.length - 1;
              return isLast ? (
                <Typography key={item.label} color="text.primary">
                  {item.label}
                </Typography>
              ) : (
                <MuiLink
                  key={item.label}
                  component={Link}
                  to={item.path || '#'}
                  color="inherit"
                  underline="hover"
                >
                  {item.label}
                </MuiLink>
              );
            })}
          </Breadcrumbs>
        )}

        <Box
          sx={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'flex-start',
            flexWrap: 'wrap',
            gap: 2,
          }}
        >
          <Box>
            <Typography variant="h4" component="h1" gutterBottom>
              {title}
            </Typography>
            {subtitle && (
              <Typography variant="subtitle1" color="text.secondary">
                {subtitle}
              </Typography>
            )}
          </Box>

          {actions && (
            <Box sx={{ display: 'flex', gap: 1, flexShrink: 0 }}>
              {actions}
            </Box>
          )}
        </Box>
      </Box>

      {/* Content */}
      <Paper
        elevation={0}
        sx={{
          p: 3,
          border: (theme) => `1px solid ${theme.palette.divider}`,
          borderRadius: 2,
        }}
      >
        {children}
      </Paper>
    </Box>
  );
}