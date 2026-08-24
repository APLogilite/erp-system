import {
  Box,
  CircularProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { PageContainer } from '@/components/layouts/PageContainer';
import { apiClient } from '@/core/api/client';

interface Partner {
  id: string;
  code: string;
  name: string;
  email?: string;
  phone?: string;
}

function fetchPartners() {
  return apiClient
    .get<{
      success: boolean;
      data: { items: Partner[] };
    }>('/runtime/windows/Business%20Partners/records?page=0&size=100&sortField=name&sortDir=asc')
    .then((res) => res.data.data.items);
}

export function Customer360SelectorPage() {
  const navigate = useNavigate();
  const { data, isLoading, error } = useQuery({
    queryKey: ['customer360Partners'],
    queryFn: fetchPartners,
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

  if (error) {
    return (
      <PageContainer title="Customer 360">
        <Typography color="error" sx={{ mt: 2 }}>
          {error instanceof Error ? error.message : 'Unable to load partners.'}
        </Typography>
      </PageContainer>
    );
  }

  return (
    <PageContainer title="Customer 360 — Select Partner">
      <TableContainer>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Code</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Email</TableCell>
              <TableCell>Phone</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {data?.map((partner) => (
              <TableRow
                key={partner.id}
                hover
                sx={{ cursor: 'pointer' }}
                onClick={() => navigate(`/app/customer-360/${partner.id}`)}
              >
                <TableCell>{partner.code}</TableCell>
                <TableCell>{partner.name}</TableCell>
                <TableCell>{partner.email || '-'}</TableCell>
                <TableCell>{partner.phone || '-'}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </PageContainer>
  );
}
