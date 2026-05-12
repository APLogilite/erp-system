import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  Avatar,
} from '@mui/material';
import {
  Inventory,
  ShoppingCart,
  People,
  TrendingUp,
} from '@mui/icons-material';
import { PageContainer } from '@/components/layouts/PageContainer';

const statsCards = [
  {
    title: 'Total Products',
    value: '1,234',
    icon: Inventory,
    color: '#1976d2',
    change: '+12%',
  },
  {
    title: 'Active Orders',
    value: '89',
    icon: ShoppingCart,
    color: '#388e3c',
    change: '+8%',
  },
  {
    title: 'Total Users',
    value: '456',
    icon: People,
    color: '#f57c00',
    change: '+15%',
  },
  {
    title: 'Revenue',
    value: '$123,456',
    icon: TrendingUp,
    color: '#d32f2f',
    change: '+23%',
  },
];

export function DashboardPage() {
  return (
    <PageContainer
      title="Dashboard"
      subtitle="Welcome to your ERP system overview"
    >
      <Grid container spacing={3}>
        {statsCards.map((card, index) => (
          <Grid item xs={12} sm={6} md={3} key={index}>
            <Card
              sx={{
                height: '100%',
                transition: 'transform 0.2s',
                '&:hover': {
                  transform: 'translateY(-4px)',
                },
              }}
            >
              <CardContent>
                <Box
                  sx={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    mb: 2,
                  }}
                >
                  <Avatar
                    sx={{
                      bgcolor: card.color + '20',
                      color: card.color,
                    }}
                  >
                    <card.icon />
                  </Avatar>
                  <Typography
                    variant="body2"
                    color="success.main"
                    sx={{ fontWeight: 600 }}
                  >
                    {card.change}
                  </Typography>
                </Box>
                <Typography variant="h4" component="div" gutterBottom>
                  {card.value}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {card.title}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}

        {/* Sample widgets */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Recent Activity
              </Typography>
              <Box sx={{ mt: 2 }}>
                <Typography variant="body2" color="text.secondary" paragraph>
                  • New order #1234 received
                </Typography>
                <Typography variant="body2" color="text.secondary" paragraph>
                  • Product inventory updated
                </Typography>
                <Typography variant="body2" color="text.secondary" paragraph>
                  • User account created
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Quick Actions
              </Typography>
              <Box sx={{ mt: 2 }}>
                <Typography variant="body2" color="text.secondary" paragraph>
                  This dashboard is ready for metadata-driven widgets and components.
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Future enhancements will include charts, graphs, and dynamic content.
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </PageContainer>
  );
}
