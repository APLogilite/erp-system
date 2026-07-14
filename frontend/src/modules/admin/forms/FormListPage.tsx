import { Add, ContentCopy, Delete, Edit, Refresh } from '@mui/icons-material';
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
  Typography,
} from '@mui/material';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { EmptyState } from '@/components/ui/EmptyState';
import { ErrorState } from '@/components/ui/ErrorState';
import { notifyActions } from '@/core/store/notifications/notificationStore';

import { CreateFormDialog } from './components/CreateFormDialog';
import { useCloneForm, useDeleteForm, useFormList } from './hooks/useFormDesigner';

export function FormListPage() {
  const navigate = useNavigate();
  const { data: forms, isLoading, error, refetch } = useFormList();
  const deleteMutation = useDeleteForm();
  const cloneMutation = useCloneForm();
  const [createOpen, setCreateOpen] = useState(false);

  const handleDelete = async (id: string, label: string) => {
    if (!window.confirm(`Delete form "${label}"?`)) return;
    try {
      await deleteMutation.mutateAsync(id);
      notifyActions.success(`Form "${label}" deleted.`);
    } catch {
      notifyActions.error('Failed to delete form.');
    }
  };

  const handleClone = async (id: string) => {
    try {
      const result = await cloneMutation.mutateAsync(id);
      notifyActions.success(`Form cloned as "${result.code}".`);
    } catch {
      notifyActions.error('Failed to clone form.');
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h5" fontWeight={700}>
          Form Designer
        </Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <IconButton onClick={() => refetch()}>
            <Refresh />
          </IconButton>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => setCreateOpen(true)}
            sx={{ borderRadius: 2, textTransform: 'none' }}
          >
            Create Form
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
        {forms && forms.length === 0 && !isLoading && (
          <EmptyState title="No forms defined" message="Create your first form to get started." />
        )}
        {forms && forms.length > 0 && (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ fontWeight: 600 }}>Code</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Label</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Model</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Scope</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Status</TableCell>
                  <TableCell sx={{ fontWeight: 600 }} width={200}>
                    Actions
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {forms.map((f) => (
                  <TableRow key={f.id} hover>
                    <TableCell>
                      <code>{f.code}</code>
                    </TableCell>
                    <TableCell>{f.label}</TableCell>
                    <TableCell>
                      <code>{f.modelName}</code>
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={f.scope}
                        size="small"
                        color={f.scope === 'global' ? 'primary' : 'secondary'}
                        variant="outlined"
                      />
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={f.isActive ? 'Active' : 'Inactive'}
                        color={f.isActive ? 'success' : 'default'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <Box sx={{ display: 'flex', gap: 0.5 }}>
                        <IconButton
                          size="small"
                          onClick={() => navigate(`/app/admin/forms/${f.id}`)}
                        >
                          <Edit fontSize="small" />
                        </IconButton>
                        <IconButton size="small" onClick={() => handleClone(f.id)}>
                          <ContentCopy fontSize="small" />
                        </IconButton>
                        <IconButton
                          size="small"
                          color="error"
                          onClick={() => handleDelete(f.id, f.label)}
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

      <CreateFormDialog
        open={createOpen}
        onClose={() => setCreateOpen(false)}
        onCreated={(id) => navigate(`/app/admin/forms/${id}`)}
      />
    </Box>
  );
}
