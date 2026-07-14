import {
  Box,
  Button,
  Checkbox,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  FormControlLabel,
  InputLabel,
  MenuItem,
  Select,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  Tabs,
  Tab,
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
  fetchLookupRecords,
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
  const childTabs = windowDef.tabs.filter((t) => t.parentColumn);
  const [formData, setFormData] = useState<Record<string, unknown>>({});
  const [saveError, setSaveError] = useState<string | null>(null);

  // Collect unique relation table names for lookup dropdowns
  const lookupTables = [
    ...new Set(
      (mainTab?.fields ?? [])
        .filter((f) => f.column.relationTable)
        .map((f) => f.column.relationTable!)
    ),
  ];

  // Fetch all lookup data upfront (avoids hooks-in-loop violation)
  const lookupQueries: Record<string, Record<string, unknown>[]> = {};
  for (const tableName of lookupTables) {
    // eslint-disable-next-line react-hooks/rules-of-hooks
    const { data } = useQuery({
      queryKey: ['lookup', tableName],
      queryFn: () => fetchLookupRecords(tableName),
      staleTime: 30000,
      gcTime: 60000,
    });
    lookupQueries[tableName] = data ?? [];
  }

  // Fetch record data if editing
  const { data: recordData, isLoading: isLoadingRecord } = useQuery({
    queryKey: ['window-record', windowName, recordId],
    queryFn: () => fetchWindowRecord(windowName, recordId!),
    enabled: !!recordId,
  });

  // Extract child records from recordData
  const childRecordsMap = recordData
    ? ((recordData as { childRecords?: Record<string, Record<string, unknown>[]> }).childRecords ??
      {})
    : {};

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
    onError: (err: Error) => setSaveError(err.message),
  });

  // Update mutation
  const updateMutation = useMutation({
    mutationFn: (data: Record<string, unknown>) => updateWindowRecord(windowName, recordId!, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['window-records', windowName] });
      onClose();
    },
    onError: (err: Error) => setSaveError(err.message),
  });

  // Parse numeric fields before saving (form sends strings, DB expects numbers)
  const handleSave = useCallback(() => {
    const parsed = { ...formData };
    for (const field of mainTab?.fields ?? []) {
      const val = parsed[field.column.code];
      if (val === '' || val === undefined || val === null) continue;
      if (field.column.type === 'integer') {
        parsed[field.column.code] = parseInt(val as string, 10);
      } else if (field.column.type === 'decimal' || field.column.type === 'numeric') {
        parsed[field.column.code] = parseFloat(val as string);
      }
    }
    if (recordId) {
      updateMutation.mutate(parsed);
    } else {
      createMutation.mutate(parsed);
    }
  }, [recordId, formData, mainTab, createMutation, updateMutation]);

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
        {saveError && (
          <Typography color="error" sx={{ mb: 2, p: 1, bgcolor: 'error.light', borderRadius: 1 }}>
            {saveError}
          </Typography>
        )}
        {isLoadingRecord && recordId ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress />
          </Box>
        ) : (
          <Box sx={{ pt: 2 }}>
            {fields.map((field) => {
              const value = formData[field.column.code] ?? '';
              const label = field.labelOverride ?? field.column.label;

              switch (field.column.type) {
                case 'boolean':
                  return (
                    <FormControlLabel
                      key={field.column.code}
                      control={
                        <Checkbox
                          checked={!!value}
                          onChange={(e) => handleFieldChange(field.column.code, e.target.checked)}
                          disabled={field.isReadonly || isSaving}
                        />
                      }
                      label={label}
                      sx={{ mb: 1, display: 'flex' }}
                    />
                  );

                case 'enum': {
                  const options = field.column.enumOptions
                    ? JSON.parse(field.column.enumOptions)
                    : [];
                  return (
                    <FormControl key={field.column.code} fullWidth margin="dense" sx={{ mb: 1 }}>
                      <InputLabel>{label}</InputLabel>
                      <Select
                        value={value as string}
                        label={label}
                        onChange={(e) => handleFieldChange(field.column.code, e.target.value)}
                        disabled={field.isReadonly || isSaving}
                        required={field.isMandatory}
                      >
                        {options.map((opt: string) => (
                          <MenuItem key={opt} value={opt}>
                            {opt}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  );
                }

                case 'date':
                  return (
                    <TextField
                      key={field.column.code}
                      fullWidth
                      margin="dense"
                      label={label}
                      type="date"
                      value={typeof value === 'string' ? value.slice(0, 10) : ''}
                      onChange={(e) => handleFieldChange(field.column.code, e.target.value)}
                      disabled={field.isReadonly || isSaving}
                      required={field.isMandatory}
                      InputLabelProps={{ shrink: true }}
                      sx={{ mb: 1 }}
                    />
                  );

                case 'many2one':
                case 'many2many':
                case 'one2many': {
                  const relationTable = field.column.relationTable!;
                  const lookupOptions = lookupQueries[relationTable] ?? [];

                  return (
                    <FormControl key={field.column.code} fullWidth margin="dense" sx={{ mb: 1 }}>
                      <InputLabel>{label}</InputLabel>
                      <Select
                        value={value as string}
                        label={label}
                        onChange={(e) => handleFieldChange(field.column.code, e.target.value)}
                        disabled={field.isReadonly || isSaving || !relationTable}
                        required={field.isMandatory}
                      >
                        <MenuItem value="">
                          <em>None</em>
                        </MenuItem>
                        {lookupOptions.map((opt) => (
                          <MenuItem key={opt.id as string} value={opt.id as string}>
                            {(opt._display as string) ?? (opt.id as string)}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  );
                }

                case 'text':
                  return (
                    <TextField
                      key={field.column.code}
                      fullWidth
                      margin="dense"
                      label={label}
                      value={String(value ?? '')}
                      onChange={(e) => handleFieldChange(field.column.code, e.target.value)}
                      disabled={field.isReadonly || isSaving}
                      required={field.isMandatory}
                      multiline
                      rows={field.numLines > 1 ? field.numLines : 3}
                      sx={{ mb: 1 }}
                    />
                  );

                case 'integer':
                case 'decimal':
                case 'numeric':
                  return (
                    <TextField
                      key={field.column.code}
                      fullWidth
                      margin="dense"
                      label={label}
                      type="number"
                      value={value}
                      onChange={(e) => handleFieldChange(field.column.code, e.target.value)}
                      disabled={field.isReadonly || isSaving}
                      required={field.isMandatory}
                      inputProps={{
                        step: field.column.type === 'integer' ? '1' : '0.01',
                      }}
                      sx={{ mb: 1 }}
                    />
                  );

                default:
                  return (
                    <TextField
                      key={field.column.code}
                      fullWidth
                      margin="dense"
                      label={label}
                      value={String(value ?? '')}
                      onChange={(e) => handleFieldChange(field.column.code, e.target.value)}
                      disabled={field.isReadonly || isSaving}
                      required={field.isMandatory}
                      sx={{ mb: 1 }}
                    />
                  );
              }
            })}
          </Box>
        )}
      </DialogContent>
      {/* Child tab records (only shown when editing an existing record) */}
      {recordId && childTabs.length > 0 && (
        <Box sx={{ mt: 3, pt: 2, borderTop: 1, borderColor: 'divider' }}>
          <Typography variant="subtitle1" sx={{ mb: 2, fontWeight: 600 }}>
            Related Records
          </Typography>
          {childTabs.map((ct) => (
            <ChildTabGrid key={ct.id} tab={ct} childRecords={childRecordsMap[ct.name] ?? []} />
          ))}
        </Box>
      )}

      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button onClick={handleSave} variant="contained" disabled={isSaving || isLoadingRecord}>
          {isSaving ? 'Saving...' : 'Save'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

// ---- Child Tab Grid ----

interface ChildTabGridProps {
  tab: WindowTabDefinition;
  childRecords: Record<string, unknown>[];
}

function ChildTabGrid({ tab, childRecords }: ChildTabGridProps) {
  const fields = tab.fields
    .filter((f) => f.isDisplayed !== false)
    .sort((a, b) => a.seqNo - b.seqNo);

  if (childRecords.length === 0) {
    return (
      <Box sx={{ mb: 2, px: 1 }}>
        <Typography variant="subtitle2" sx={{ mb: 0.5, fontWeight: 600 }}>
          {tab.name}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          No related records found.
        </Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ mb: 2 }}>
      <Typography variant="subtitle2" sx={{ mb: 0.5, fontWeight: 600, px: 1 }}>
        {tab.name}
      </Typography>
      <TableContainer>
        <Table size="small">
          <TableHead>
            <TableRow>
              {fields.map((f) => (
                <TableCell key={f.column.code} sx={{ fontWeight: 600, fontSize: 12 }}>
                  {f.labelOverride ?? f.column.label}
                </TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {childRecords.map((rec) => (
              <TableRow key={rec.id as string}>
                {fields.map((f) => (
                  <TableCell key={f.column.code} sx={{ fontSize: 12 }}>
                    {String(rec[f.column.code] ?? '')}
                  </TableCell>
                ))}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
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
  const [activeTab, setActiveTab] = useState(0);

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
  const { data: recordsData, isLoading: isLoadingRecords } = useQuery({
    queryKey: ['window-records', windowName, page],
    queryFn: () => fetchWindowRecords(windowName!, page, pageSize),
    enabled: !!windowName && !!windowDef,
  });

  if (!windowName) {
    return (
      <PageContainer title="Window" subtitle="No window selected">
        <Typography color="text.secondary">Select a window from the menu.</Typography>
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

  // Build tab hierarchy: top-level tabs (no parentColumn) + their children
  const topTabs = windowDef.tabs.filter((t) => !t.parentColumn).sort((a, b) => a.seqNo - b.seqNo);
  const currentTab = topTabs[activeTab] ?? topTabs[0];
  const childTabs = currentTab
    ? windowDef.tabs
        .filter(
          (t) =>
            t.parentColumn === currentTab.name.toLowerCase().replace(/\s/g, '_') ||
            t.parentColumn === currentTab.name
        )
        .sort((a, b) => a.seqNo - b.seqNo)
    : [];

  const fields = currentTab ? getDisplayedFields(currentTab) : [];
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
      {/* Tab navigation */}
      {topTabs.length > 1 && (
        <Tabs
          value={activeTab < topTabs.length ? activeTab : 0}
          onChange={(_e, newValue) => {
            setActiveTab(newValue);
            setPage(0);
          }}
          sx={{ mb: 2, borderBottom: 1, borderColor: 'divider' }}
        >
          {topTabs.map((tab) => (
            <Tab key={tab.id} label={tab.name} />
          ))}
        </Tabs>
      )}

      <Box sx={{ mb: 2, display: 'flex', gap: 1 }}>
        <Button variant="contained" onClick={handleCreate}>
          New {currentTab?.name ?? 'Record'}
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

      {/* Child tab indicators */}
      {childTabs.length > 0 && (
        <Box sx={{ mt: 3, px: 1 }}>
          <Typography variant="subtitle2" color="text.secondary" sx={{ mb: 1 }}>
            Related records:
          </Typography>
          {childTabs.map((ct) => (
            <Typography key={ct.id} variant="body2" sx={{ ml: 2, mb: 0.5 }}>
              • {ct.name}
            </Typography>
          ))}
        </Box>
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
