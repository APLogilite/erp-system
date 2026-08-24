import {
  Box,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Divider,
  Grid,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';

import { PageContainer } from '@/components/layouts/PageContainer';
import { apiClient } from '@/core/api/client';

interface Customer360Data {
  partner: {
    id: string;
    code: string;
    name: string;
    email?: string;
    phone?: string;
    creditLimit?: number;
    creditHold?: boolean;
    paymentTermId?: string;
  };
  outstanding: number;
  recentOrders: Array<{
    id: string;
    orderNumber: string;
    orderDate: string;
    grandTotal: number;
    status: string;
  }>;
  recentInvoices: Array<{
    id: string;
    invoiceNumber: string;
    invoiceDate: string;
    grandTotal: number;
    dueAmount: number;
    status: string;
  }>;
  recentPayments: Array<{
    id: string;
    paymentNumber: string;
    paymentDate: string;
    amount: number;
    status: string;
  }>;
  openQuotations: Array<{
    id: string;
    quoteNumber: string;
    quoteDate: string;
    grandTotal: number;
    validUntil?: string;
    status: string;
  }>;
}

function fetchCustomer360(partnerId: string | undefined) {
  if (!partnerId) return Promise.reject(new Error('Partner ID is required'));
  return apiClient
    .get<{ success: boolean; data: Customer360Data }>(`/customers/${partnerId}/360`)
    .then((res) => res.data.data);
}

function formatCurrency(value: number | undefined) {
  if (value === undefined || value === null) return '-';
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
}

function formatDate(value: string | undefined) {
  if (!value) return '-';
  return new Date(value).toLocaleDateString();
}

export function Customer360Page() {
  const { partnerId } = useParams<{ partnerId: string }>();
  const { data, isLoading, error } = useQuery({
    queryKey: ['customer360', partnerId],
    queryFn: () => fetchCustomer360(partnerId),
    enabled: Boolean(partnerId),
  });

  if (isLoading) {
    return (
      <PageContainer title="Customer 360">
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
          <CircularProgress />
        </Box>
      </PageContainer>
    );
  }

  if (error || !data) {
    return (
      <PageContainer title="Customer 360">
        <Typography color="error" sx={{ mt: 2 }}>
          {error instanceof Error ? error.message : 'Unable to load customer 360 view.'}
        </Typography>
      </PageContainer>
    );
  }

  const { partner, outstanding, recentOrders, recentInvoices, recentPayments, openQuotations } =
    data;

  return (
    <PageContainer title={`Customer 360 — ${partner.name}`}>
      <Grid container spacing={2}>
        {/* Profile & Credit */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Profile
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Code
              </Typography>
              <Typography variant="body1" gutterBottom>
                {partner.code}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Email
              </Typography>
              <Typography variant="body1" gutterBottom>
                {partner.email || '-'}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Phone
              </Typography>
              <Typography variant="body1" gutterBottom>
                {partner.phone || '-'}
              </Typography>
              <Divider sx={{ my: 2 }} />
              <Typography variant="h6" gutterBottom>
                Credit
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Credit Limit
              </Typography>
              <Typography variant="body1" gutterBottom>
                {formatCurrency(partner.creditLimit)}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Outstanding
              </Typography>
              <Typography variant="body1" gutterBottom>
                {formatCurrency(outstanding)}
              </Typography>
              {partner.creditHold && <Chip label="Credit Hold" color="error" size="small" />}
            </CardContent>
          </Card>
        </Grid>

        {/* Recent Orders */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Recent Orders
              </Typography>
              <TableContainer>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Order #</TableCell>
                      <TableCell>Date</TableCell>
                      <TableCell>Status</TableCell>
                      <TableCell align="right">Total</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {recentOrders.length === 0 ? (
                      <TableRow>
                        <TableCell colSpan={4}>No recent orders</TableCell>
                      </TableRow>
                    ) : (
                      recentOrders.map((row) => (
                        <TableRow key={row.id}>
                          <TableCell>{row.orderNumber}</TableCell>
                          <TableCell>{formatDate(row.orderDate)}</TableCell>
                          <TableCell>{row.status}</TableCell>
                          <TableCell align="right">{formatCurrency(row.grandTotal)}</TableCell>
                        </TableRow>
                      ))
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
        </Grid>

        {/* Recent Invoices */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Recent Invoices
              </Typography>
              <TableContainer>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Invoice #</TableCell>
                      <TableCell>Date</TableCell>
                      <TableCell>Status</TableCell>
                      <TableCell align="right">Total</TableCell>
                      <TableCell align="right">Due</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {recentInvoices.length === 0 ? (
                      <TableRow>
                        <TableCell colSpan={5}>No recent invoices</TableCell>
                      </TableRow>
                    ) : (
                      recentInvoices.map((row) => (
                        <TableRow key={row.id}>
                          <TableCell>{row.invoiceNumber}</TableCell>
                          <TableCell>{formatDate(row.invoiceDate)}</TableCell>
                          <TableCell>{row.status}</TableCell>
                          <TableCell align="right">{formatCurrency(row.grandTotal)}</TableCell>
                          <TableCell align="right">{formatCurrency(row.dueAmount)}</TableCell>
                        </TableRow>
                      ))
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
        </Grid>

        {/* Recent Payments */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Recent Payments
              </Typography>
              <TableContainer>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Payment #</TableCell>
                      <TableCell>Date</TableCell>
                      <TableCell>Status</TableCell>
                      <TableCell align="right">Amount</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {recentPayments.length === 0 ? (
                      <TableRow>
                        <TableCell colSpan={4}>No recent payments</TableCell>
                      </TableRow>
                    ) : (
                      recentPayments.map((row) => (
                        <TableRow key={row.id}>
                          <TableCell>{row.paymentNumber}</TableCell>
                          <TableCell>{formatDate(row.paymentDate)}</TableCell>
                          <TableCell>{row.status}</TableCell>
                          <TableCell align="right">{formatCurrency(row.amount)}</TableCell>
                        </TableRow>
                      ))
                    )}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
        </Grid>

        {/* Open Quotations */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Open Quotations
              </Typography>
              <TableContainer>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Quote #</TableCell>
                      <TableCell>Date</TableCell>
                      <TableCell>Valid Until</TableCell>
                      <TableCell>Status</TableCell>
                      <TableCell align="right">Total</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {openQuotations.length === 0 ? (
                      <TableRow>
                        <TableCell colSpan={5}>No open quotations</TableCell>
                      </TableRow>
                    ) : (
                      openQuotations.map((row) => (
                        <TableRow key={row.id}>
                          <TableCell>{row.quoteNumber}</TableCell>
                          <TableCell>{formatDate(row.quoteDate)}</TableCell>
                          <TableCell>{formatDate(row.validUntil)}</TableCell>
                          <TableCell>{row.status}</TableCell>
                          <TableCell align="right">{formatCurrency(row.grandTotal)}</TableCell>
                        </TableRow>
                      ))
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
