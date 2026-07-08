import { Add, Delete, Refresh, Visibility } from '@mui/icons-material';
import {
  Box,
  Button,
  Card,
  Chip,
  CircularProgress,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from '@mui/material';
import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { useDeleteTable, useTableList } from './hooks/useTables';

import { EmptyState } from '@/components/ui/EmptyState';
import { ErrorState } from '@/components/ui/ErrorState';
import { notifyActions } from '@/core/store/notifications/notificationStore';

export function TableListPage() {
  const navigate = useNavigate();
  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const timerRef = useRef<ReturnType<typeof setTimeout>>();

  useEffect(() => {
    if (timerRef.current) clearTimeout(timerRef.current);
    timerRef.current = setTimeout(() => setDebouncedSearch(search), 300);
    return () => {
      if (timerRef.current) clearTimeout(timerRef.current);
    };
  }, [search]);

  const { data, isLoading, error, refetch } = useTableList(debouncedSearch || undefined);
  const deleteMutation = useDeleteTable();

  const handleDelete = async (id: string, label: string) => {
    if (!window.confirm(`Deactivate table "${label}"?`)) return;
    try {
      await deleteMutation.mutateAsync(id);
      notifyActions.success(`Table "${label}" deactivated.`);
    } catch {
      notifyActions.error('Failed to deactivate table.');
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h5" fontWeight={700}>
          Table Designer
        </Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <TextField
            size="small"
            placeholder="Search tables..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            sx={{ width: 240 }}
          />
          <IconButton onClick={() => refetch()}>
            <Refresh />
          </IconButton>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => navigate('/app/admin/tables/create')}
            sx={{ borderRadius: 2, textTransform: 'none' }}
          >
            Create Table
          </Button>
        </Box>
      </Box>

      <Card sx={{ borderRadius: 3 }}>
        {error && <ErrorState message={(error as Error).message} onRetry={refetch} />}
        {isLoading && (
          <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
            <CircularProgress />
          </Box>
        )}
        {data && data.length === 0 && !isLoading && (
          <EmptyState title="No tables defined" message="Create your first table to get started." />
        )}
        {data && data.length > 0 && (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ fontWeight: 600 }}>Code</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Label</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Table Name</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Columns</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Status</TableCell>
                  <TableCell sx={{ fontWeight: 600 }} width={160}>
                    Actions
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {data.map((t) => (
                  <TableRow key={t.id} hover>
                    <TableCell>
                      <code>{t.code}</code>
                    </TableCell>
                    <TableCell>{t.label}</TableCell>
                    <TableCell>
                      <code>{t.tableName}</code>
                    </TableCell>
                    <TableCell>{t.columns?.length ?? 0}</TableCell>
                    <TableCell>
                      <Chip
                        label={t.isActive ? 'Active' : 'Inactive'}
                        color={t.isActive ? 'success' : 'default'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', gap: 0.5 }}>
                        <IconButton
                          size="small"
                          onClick={() => navigate(`/app/admin/tables/${t.id}`)}
                        >
                          <Visibility fontSize="small" />
                        </IconButton>
                        <IconButton
                          size="small"
                          color="error"
                          onClick={() => handleDelete(t.id, t.label)}
                          disabled={deleteMutation.isPending}
                        >
                          <Delete fontSize="small" />
                        </IconButton>
                      </Box>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Card>
    </Box>
  );
}
