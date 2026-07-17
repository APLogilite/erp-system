import {
  AccountTree,
  Business,
  CorporateFare,
  Devices,
  Group,
  History,
  People,
  Security,
  Store,
} from '@mui/icons-material';
import { Box, Card, CardActionArea, CardContent, Grid, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';

interface AdminModule {
  title: string;
  description: string;
  icon: React.ElementType;
  path: string;
}

const modules: AdminModule[] = [
  {
    title: 'Tenants',
    description: 'Manage system tenants',
    icon: Business,
    path: '/app/admin/tenants',
  },
  {
    title: 'Organizations',
    description: 'Manage organizations',
    icon: CorporateFare,
    path: '/app/admin/organizations',
  },
  {
    title: 'Companies',
    description: 'Manage companies',
    icon: Store,
    path: '/app/admin/companies',
  },
  {
    title: 'Branches',
    description: 'Manage branches',
    icon: AccountTree,
    path: '/app/admin/branches',
  },
  {
    title: 'Departments',
    description: 'Manage departments',
    icon: AccountTree,
    path: '/app/admin/departments',
  },
  { title: 'Users', description: 'Manage user accounts', icon: People, path: '/app/admin/users' },
  {
    title: 'Roles',
    description: 'Manage roles and permissions',
    icon: Group,
    path: '/app/admin/roles',
  },
  {
    title: 'Permissions',
    description: 'Manage permission codes',
    icon: Security,
    path: '/app/admin/permissions',
  },
  {
    title: 'Sessions',
    description: 'Manage active sessions',
    icon: Devices,
    path: '/app/admin/sessions',
  },
  { title: 'Audit Log', description: 'View audit trail', icon: History, path: '/app/admin/audit' },
];

export function AdminDashboardPage() {
  const navigate = useNavigate();

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h5" fontWeight={700} sx={{ mb: 3 }}>
        Administration
      </Typography>
      <Grid container spacing={2}>
        {modules.map((m) => (
          <Grid item xs={12} sm={6} md={4} lg={3} key={m.path}>
            <Card sx={{ borderRadius: 3 }} elevation={1}>
              <CardActionArea onClick={() => navigate(m.path)} sx={{ p: 2 }}>
                <CardContent sx={{ p: 0, '&:last-child': { pb: 0 } }}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 1 }}>
                    <m.icon color="primary" />
                    <Typography variant="subtitle1" fontWeight={600}>
                      {m.title}
                    </Typography>
                  </Box>
                  <Typography variant="body2" color="text.secondary">
                    {m.description}
                  </Typography>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}
