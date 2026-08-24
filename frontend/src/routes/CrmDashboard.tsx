import {
  Box,
  Card,
  CardContent,
  CircularProgress,
  Grid,
  LinearProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';
import { useQuery } from '@tanstack/react-query';

import { PageContainer } from '@/components/layouts/PageContainer';
import { apiClient } from '@/core/api/client';

interface PipelineStageSummary {
  count: number;
  totalRevenue: number;
}

interface TeamPerformanceRow {
  salespersonId: string;
  name: string;
  openOpportunities: number;
  closedRevenue: number;
  commissionEarned: number;
}

interface CrmDashboardData {
  pipeline: {
    qualification: PipelineStageSummary;
    proposal: PipelineStageSummary;
    negotiation: PipelineStageSummary;
    closedWon: PipelineStageSummary;
    closedLost: PipelineStageSummary;
  };
  conversionRates: {
    leadToOpportunity: number;
    opportunityToOrder: number;
  };
  teamPerformance: TeamPerformanceRow[];
}

function fetchDashboard() {
  return apiClient
    .get<{ success: boolean; data: CrmDashboardData }>('/crm/dashboard')
    .then((res) => res.data.data);
}

function formatCurrency(value: number | undefined) {
  if (value === undefined || value === null) return '-';
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
}

function formatPercent(value: number | undefined) {
  if (value === undefined || value === null) return '-';
  return new Intl.NumberFormat('en-US', { style: 'percent', minimumFractionDigits: 1 }).format(
    value
  );
}

const PIPELINE_STAGES: { key: keyof CrmDashboardData['pipeline']; label: string; color: string }[] =
  [
    { key: 'qualification', label: 'Qualification', color: '#90caf9' },
    { key: 'proposal', label: 'Proposal', color: '#a5d6a7' },
    { key: 'negotiation', label: 'Negotiation', color: '#fff59d' },
    { key: 'closedWon', label: 'Closed Won', color: '#81c784' },
    { key: 'closedLost', label: 'Closed Lost', color: '#ef5350' },
  ];

export default function CrmDashboard() {
  const { data, isLoading } = useQuery({
    queryKey: ['crm-dashboard'],
    queryFn: fetchDashboard,
  });

  if (isLoading) {
    return (
      <PageContainer title="CRM Dashboard">
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
          <CircularProgress />
        </Box>
      </PageContainer>
    );
  }

  const pipeline = data?.pipeline;
  const totalRevenue = pipeline
    ? Object.values(pipeline).reduce((sum, stage) => sum + (stage?.totalRevenue || 0), 0)
    : 0;

  return (
    <PageContainer
      title="CRM Dashboard"
      subtitle="Pipeline, conversion rates, and team performance"
    >
      <Grid container spacing={3}>
        {PIPELINE_STAGES.map((stage) => {
          const summary = pipeline?.[stage.key];
          const revenue = summary?.totalRevenue || 0;
          const pct = totalRevenue > 0 ? revenue / totalRevenue : 0;
          return (
            <Grid item xs={12} sm={6} md={4} key={stage.key}>
              <Card variant="outlined">
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {stage.label}
                  </Typography>
                  <Typography variant="h4">{summary?.count ?? 0}</Typography>
                  <Typography color="text.secondary" variant="body2">
                    {formatCurrency(revenue)}
                  </Typography>
                  <Box sx={{ mt: 2 }}>
                    <LinearProgress
                      variant="determinate"
                      value={pct * 100}
                      sx={{
                        height: 8,
                        borderRadius: 1,
                        backgroundColor: '#e0e0e0',
                        '& .MuiLinearProgress-bar': { backgroundColor: stage.color },
                      }}
                    />
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          );
        })}

        <Grid item xs={12} md={6}>
          <Card variant="outlined" sx={{ height: '100%' }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Conversion Rates
              </Typography>
              <Box sx={{ mt: 2 }}>
                <Typography variant="body1">
                  Lead → Opportunity:{' '}
                  <strong>{formatPercent(data?.conversionRates?.leadToOpportunity)}</strong>
                </Typography>
                <Typography variant="body1" sx={{ mt: 1 }}>
                  Opportunity → Order:{' '}
                  <strong>{formatPercent(data?.conversionRates?.opportunityToOrder)}</strong>
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card variant="outlined" sx={{ height: '100%' }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Team Performance
              </Typography>
              <TableContainer>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Salesperson</TableCell>
                      <TableCell align="right">Open Opps</TableCell>
                      <TableCell align="right">Closed Revenue</TableCell>
                      <TableCell align="right">Commission Earned</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {data?.teamPerformance?.length ? (
                      data.teamPerformance.map((row) => (
                        <TableRow key={row.salespersonId}>
                          <TableCell>{row.name}</TableCell>
                          <TableCell align="right">{row.openOpportunities}</TableCell>
                          <TableCell align="right">{formatCurrency(row.closedRevenue)}</TableCell>
                          <TableCell align="right">
                            {formatCurrency(row.commissionEarned)}
                          </TableCell>
                        </TableRow>
                      ))
                    ) : (
                      <TableRow>
                        <TableCell colSpan={4} align="center">
                          No team data available
                        </TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </PageContainer>
  );
}
