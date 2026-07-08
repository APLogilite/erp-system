import { ArrowBack, Refresh } from '@mui/icons-material';
import {
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  IconButton,
  Tab,
  Tabs,
  Typography,
} from '@mui/material';
import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { ColumnFormDialog } from './components/ColumnFormDialog';
import { ColumnList } from './components/ColumnList';
import { SchemaHistoryTimeline } from './components/SchemaHistoryTimeline';
import {
  useAddColumn,
  useDeleteColumn,
  useReorderColumns,
  useUpdateColumn,
} from './hooks/useColumns';
import { useTable, useTableHistory } from './hooks/useTables';
import type { CreateColumnPayload, TableColumn, UpdateColumnPayload } from './types';

import { ErrorState } from '@/components/ui/ErrorState';
import { notifyActions } from '@/core/store/notifications/notificationStore';

export function TableDetailPage() {
  const { tableId } = useParams<{ tableId: string }>();
  const navigate = useNavigate();

  const { data: table, isLoading, error, refetch } = useTable(tableId);
  const { data: history, isLoading: historyLoading } = useTableHistory(tableId);

  const addColumn = useAddColumn(tableId!);
  const updateColumn = useUpdateColumn(tableId!);
  const deleteColumn = useDeleteColumn(tableId!);
  const reorderColumns = useReorderColumns(tableId!);

  const [tab, setTab] = useState(0);
  const [colDialogOpen, setColDialogOpen] = useState(false);
  const [editColumn, setEditColumn] = useState<TableColumn | null>(null);

  const handleAdd = () => {
    setEditColumn(null);
    setColDialogOpen(true);
  };

  const handleEdit = (col: TableColumn) => {
    setEditColumn(col);
    setColDialogOpen(true);
  };

  const handleSaveColumn = async (payload: CreateColumnPayload) => {
    if (editColumn) {
      const updatePayload: UpdateColumnPayload = {
        label: payload.label,
        type: payload.type,
        required: payload.required,
        defaultValue: payload.defaultValue,
        maxLength: payload.maxLength,
        precision: payload.precision,
        scale: payload.scale,
        relationTable: payload.relationTable,
        enumOptions: payload.enumOptions,
      };
      await updateColumn.mutateAsync({ colId: editColumn.id, payload: updatePayload });
      notifyActions.success(`Column "${payload.code}" updated.`);
    } else {
      await addColumn.mutateAsync(payload);
      notifyActions.success(`Column "${payload.code}" added.`);
    }
    return Promise.resolve();
  };

  const handleDeleteColumn = async (col: TableColumn) => {
    if (
      !window.confirm(
        `Delete column "${col.label}"? This will drop the column from the physical table.`
      )
    )
      return;
    try {
      await deleteColumn.mutateAsync(col.id);
      notifyActions.success(`Column "${col.label}" deleted.`);
    } catch {
      notifyActions.error('Failed to delete column.');
    }
  };

  const handleMoveUp = (_col: TableColumn, index: number) => {
    if (!table?.columns) return;
    const active = table.columns.filter((c) => c.isActive);
    if (index <= 0) return;
    const ids = active.map((c) => c.id);
    [ids[index - 1], ids[index]] = [ids[index], ids[index - 1]];
    reorderColumns.mutate({ columnIds: ids });
  };

  const handleMoveDown = (_col: TableColumn, index: number) => {
    if (!table?.columns) return;
    const active = table.columns.filter((c) => c.isActive);
    if (index >= active.length - 1) return;
    const ids = active.map((c) => c.id);
    [ids[index], ids[index + 1]] = [ids[index + 1], ids[index]];
    reorderColumns.mutate({ columnIds: ids });
  };

  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 8 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return <ErrorState message={(error as Error).message} onRetry={refetch} />;
  }

  if (!table) return null;

  return (
    <Box sx={{ p: 3 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate('/app/admin/tables')}
          sx={{ textTransform: 'none' }}
        >
          Back
        </Button>
        <Typography variant="h5" fontWeight={700}>
          {table.label}
        </Typography>
        <IconButton onClick={() => refetch()}>
          <Refresh />
        </IconButton>
      </Box>

      {/* Info chips */}
      <Box sx={{ display: 'flex', gap: 1, mb: 3, flexWrap: 'wrap' }}>
        <Chip label={`Code: ${table.code}`} size="small" variant="outlined" />
        <Chip label={`Table: ${table.tableName}`} size="small" variant="outlined" />
        <Chip
          label={table.isActive ? 'Active' : 'Inactive'}
          size="small"
          color={table.isActive ? 'success' : 'default'}
        />
        {table.description && (
          <Typography variant="body2" color="text.secondary" sx={{ width: '100%' }}>
            {table.description}
          </Typography>
        )}
      </Box>

      {/* Tabs */}
      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 2 }}>
        <Tab label={`Columns (${table.columns?.length ?? 0})`} />
        <Tab label="Schema History" />
        <Tab label="Forms" />
      </Tabs>

      <Card sx={{ borderRadius: 3 }}>
        <CardContent>
          {tab === 0 && table.columns && (
            <ColumnList
              columns={table.columns}
              onAdd={handleAdd}
              onEdit={handleEdit}
              onDelete={handleDeleteColumn}
              onMoveUp={handleMoveUp}
              onMoveDown={handleMoveDown}
            />
          )}

          {tab === 1 && <SchemaHistoryTimeline entries={history} isLoading={historyLoading} />}

          {tab === 2 && (
            <Typography color="text.secondary" sx={{ p: 2 }}>
              Form usage information will be available in a future update.
            </Typography>
          )}
        </CardContent>
      </Card>

      <ColumnFormDialog
        open={colDialogOpen}
        editColumn={editColumn}
        onClose={() => {
          setColDialogOpen(false);
          setEditColumn(null);
        }}
        onSave={handleSaveColumn}
      />
    </Box>
  );
}
