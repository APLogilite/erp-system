import {
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TextField,
  Typography,
} from '@mui/material';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useCallback, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

import { PageContainer } from '@/components/layouts/PageContainer';
import {
  fetchWindowDefinition,
  fetchWindowRecords,
  fetchWindowRecord,
  createWindowRecord,
  updateWindowRecord,
  deleteWindowRecord,
  type WindowDefinition,
  type WindowTabDefinition,
  type WindowFieldDefinition,
} from '@/core/runtime/api/runtimeApi';

// ---- Helper: get displayed fields from the main tab ----

function getDisplayedFields(tab: WindowTabDefinition): WindowFieldDefinition[] {
  return tab.fields.filter((f) => f.isDisplayed !== false).sort((a, b) => a.seqNo - b.seqNo);
}

// ---- Record Dialog ----

interface RecordDialogProps {
  open: boolean;
  windowName: string;
  windowDef: WindowDefinition;
  recordId?: string;
  onClose: () => void;
}

function RecordDialog({ open, windowName, windowDef, recordId, onClose }: RecordDialogProps) {
  const queryClient = useQueryClient();
  const mainTab = windowDef.tabs.find((t) => !t.parentColumn);
  const [formData, setFormData] = useState<Record<string, unknown>>({});

  // Fetch record data if editing
  const { data: recordData, isLoading: isLoadingRecord } = useQuery({
    queryKey: ['window-record', windowName, recordId],
    queryFn: () => fetchWindowRecord(windowName, recordId!),
    enabled: !!recordId,
  });

  // Initialize form data when record data loads
  useEffect(() => {
    if (recordData) {
      const record = (recordData as { record?: Record<string, unknown> }).record ?? recordData;
      setFormData(record as Record<string, unknown>);
    }
  }, [recordData]);

  // Create mutation
  const createMutation = useMutation({
    mutationFn: (data: Record<string, unknown>) => createWindowRecord(windowName, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['window-records', windowName] });
      onClose();
    },
  });

  // Update mutation
  const updateMutation = useMutation({
    mutationFn: (data: Record<string, unknown>) => updateWindowRecord(windowName, recordId!, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['window-records', windowName] });
      onClose();
    },
  });

  const handleSave = useCallback(() => {
    if (recordId) {
      updateMutation.mutate(formData);
    } else {
      createMutation.mutate(formData);
    }
  }, [recordId, formData, createMutation, updateMutation]);

  const handleFieldChange = useCallback((fieldCode: string, value: unknown) => {
    setFormData((prev) => ({ ...prev, [fieldCode]: value }));
  }, []);

  const isSaving = createMutation.isPending || updateMutation.isPending;

  if (!mainTab) {
    return (
      <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
        <DialogTitle>Error</DialogTitle>
        <DialogContent>
          <Typography>Window has no main tab.</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose}>Close</Button>
        </DialogActions>
      </Dialog>
    );
  }

  const fields = getDisplayedFields(mainTab);

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>
        {recordId ? 'Edit Record' : 'New Record'} - {windowDef.window.name}
      </DialogTitle>
      <DialogContent>
        {isLoadingRecord && recordId ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress />
          </Box>
        ) : (
          <Box sx={{ pt: 2 }}>
            {fields.map((field) => (
              <TextField
                key={field.column.code}
                fullWidth
                margin="dense"
                label={field.labelOverride ?? field.column.label}
                value={formData[field.column.code] ?? ''}
                onChange={(e) => handleFieldChange(field.column.code, e.target.value)}
                disabled={field.isReadonly || isSaving}
                required={field.isMandatory}
                type={field.column.type === 'integer' || field.column.type === 'decimal' ? 'number' : 'text'}
                multiline={field.column.type === 'text'}
                rows={field.numLines > 1 ? field.numLines : undefined}
                sx={{ mb: 1 }}
              />
            ))}
          </Box>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button
          onClick={handleSave}
          variant="contained"
          disabled={isSaving || isLoadingRecord}
        >
          {isSaving ? 'Saving...' : 'Save'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

// ---- Main WindowPage Component ----

/**
 * WindowPage — renders a window from the new Window/Tab/Field metadata.
 *
 * Shows a list view of records from the window's main tab table.
 * Clicking a record opens a detail dialog for editing.
 */
export function WindowPage() {
  const { windowName } = useParams<{ windowName: string }>();
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [pageSize] = useState(20);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editRecordId, setEditRecordId] = useState<string | undefined>();

  // Fetch window definition
  const {
    data: windowDef,
    isLoading: isLoadingDef,
    error: defError,
  } = useQuery({
    queryKey: ['window-definition', windowName],
    queryFn: () => fetchWindowDefinition(windowName!),
    enabled: !!windowName,
  });

  // Fetch records
  const {
    data: recordsData,
    isLoading: isLoadingRecords,
  } = useQuery({
    queryKey: ['window-records', windowName, page],
    queryFn: () => fetchWindowRecords(windowName!, page, pageSize),
    enabled: !!windowName && !!windowDef,
  });

  if (!windowName) {
    return (
      <PageContainer title="Window" subtitle="No window selected">
        <Typography color="text.secondary">
          Select a window from the menu.
        </Typography>
      </PageContainer>
    );
  }

  if (isLoadingDef) {
    return (
      <PageContainer title="Loading..." subtitle={`Loading ${windowName}...`}>
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
          <CircularProgress />
        </Box>
      </PageContainer>
    );
  }

  if (defError || !windowDef) {
    return (
      <PageContainer title="Error" subtitle={`Could not load "${windowName}"`}>
        <Typography color="error">
          {defError instanceof Error ? defError.message : 'Failed to load window definition.'}
        </Typography>
      </PageContainer>
    );
  }

  const mainTab = windowDef.tabs.find((t) => !t.parentColumn);
  const fields = mainTab ? getDisplayedFields(mainTab) : [];
  const records = (recordsData as { items?: Record<string, unknown>[] })?.items ?? [];
  const totalRecords = (recordsData as { total?: number })?.total ?? 0;

  const handleCreate = () => {
    setEditRecordId(undefined);
    setDialogOpen(true);
  };

  const handleEdit = (id: string) => {
    setEditRecordId(id);
    setDialogOpen(true);
  };

  const handleDelete = (id: string) => {
    if (window.confirm('Delete this record?')) {
      deleteWindowRecord(windowName, id).then(() => {
        queryClient.invalidateQueries({ queryKey: ['window-records', windowName] });
      });
    }
  };

  const handleDialogClose = () => {
    setDialogOpen(false);
    setEditRecordId(undefined);
  };

  return (
    <PageContainer
      title={windowDef.window.name}
      subtitle={windowDef.window.description ?? undefined}
    >
      <Box sx={{ mb: 2, display: 'flex', gap: 1 }}>
        <Button variant="contained" onClick={handleCreate}>
          New Record
        </Button>
      </Box>

      {isLoadingRecords ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
          <CircularProgress />
        </Box>
      ) : records.length === 0 ? (
        <Typography color="text.secondary">No records found.</Typography>
      ) : (
        <>
          <TableContainer>
            <Table size="small">
              <TableHead>
                <TableRow>
                  {fields.map((f) => (
                    <TableCell key={f.column.code} sx={{ fontWeight: 600 }}>
                      {f.labelOverride ?? f.column.label}
                    </TableCell>
                  ))}
                  <TableCell sx={{ fontWeight: 600 }}>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {records.map((record) => (
                  <TableRow
                    key={record.id as string}
                    hover
                    sx={{ cursor: 'pointer' }}
                    onClick={() => handleEdit(record.id as string)}
                  >
                    {fields.map((f) => (
                      <TableCell key={f.column.code}>
                        {String(record[f.column.code] ?? '')}
                      </TableCell>
                    ))}
                    <TableCell>
                      <Button
                        size="small"
                        color="error"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleDelete(record.id as string);
                        }}
                      >
                        Delete
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <TablePagination
            component="div"
            count={totalRecords}
            page={page}
            onPageChange={(_, newPage) => setPage(newPage)}
            rowsPerPage={pageSize}
            rowsPerPageOptions={[pageSize]}
          />
        </>
      )}

      <RecordDialog
        open={dialogOpen}
        windowName={windowName}
        windowDef={windowDef}
        recordId={editRecordId}
        onClose={handleDialogClose}
      />
    </PageContainer>
  );
}
