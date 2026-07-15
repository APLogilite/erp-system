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
import { useMutation, useQueries, useQuery, useQueryClient } from '@tanstack/react-query';
import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

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
  const [activeDialogTab, setActiveDialogTab] = useState(0);
  const navigate = useNavigate();

  // Collect unique relation table names for lookup dropdowns
  const lookupTables = [
    ...new Set(
      (windowDef.tabs.flatMap((t) => t.fields) ?? [])
        .filter((f) => f.column.relationTable)
        .map((f) => f.column.relationTable!)
    ),
  ];

  // Fetch ALL lookups — useQueries always returns same number of results
  const lookupResults = useQueries({
    queries: lookupTables.map((tableName) => ({
      queryKey: ['lookup', tableName],
      queryFn: () => fetchLookupRecords(tableName),
      staleTime: 30000,
      gcTime: 60000,
    })),
  });

  const lookupQueries: Record<string, Record<string, unknown>[]> = {};
  lookupTables.forEach((tableName, idx) => {
    lookupQueries[tableName] = lookupResults[idx]?.data ?? [];
  });

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
  const dialogTabs = [{ id: '_form', name: 'Form' }, ...childTabs];
  const currentDialogTab = dialogTabs[activeDialogTab] ?? dialogTabs[0];

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle sx={{ pb: 0 }}>
        {recordId ? 'Edit Record' : 'New Record'} - {windowDef.window.name}
      </DialogTitle>

      {/* Tab bar: Form | ChildTab1 | ChildTab2 | ... */}
      <Tabs
        value={activeDialogTab < dialogTabs.length ? activeDialogTab : 0}
        onChange={(_, v) => setActiveDialogTab(v)}
        sx={{ px: 2, borderBottom: 1, borderColor: 'divider' }}
      >
        {dialogTabs.map((t) => (
          <Tab key={t.id} label={t.name} />
        ))}
      </Tabs>

      <DialogContent sx={{ mt: 1 }}>
        {saveError && (
          <Typography color="error" sx={{ mb: 2, p: 1, bgcolor: 'error.light', borderRadius: 1 }}>
            {saveError}
          </Typography>
        )}

        {/* Form tab: show parent record fields */}
        {currentDialogTab.id === '_form' &&
          (isLoadingRecord && recordId ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
              <CircularProgress />
            </Box>
          ) : (
            <Box sx={{ pt: 1 }}>
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
                    const lookupOptions = lookupQueries[field.column.relationTable!] ?? [];
                    return (
                      <FormControl key={field.column.code} fullWidth margin="dense" sx={{ mb: 1 }}>
                        <InputLabel>{label}</InputLabel>
                        <Select
                          value={value as string}
                          label={label}
                          onChange={(e) => handleFieldChange(field.column.code, e.target.value)}
                          disabled={field.isReadonly || isSaving || !field.column.relationTable}
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
                        inputProps={{ step: field.column.type === 'integer' ? '1' : '0.01' }}
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
          ))}

        {/* Child tab: show child records grid */}
        {currentDialogTab.id !== '_form' && recordId && (
          <ChildTabGrid
            tab={currentDialogTab as WindowTabDefinition}
            childRecords={childRecordsMap[(currentDialogTab as WindowTabDefinition).name] ?? []}
            onNavigate={(tableName: string) => {
              onClose();
              navigate(`/app/window/${tableName}`);
            }}
          />
        )}
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button onClick={handleSave} variant="contained" disabled={isSaving || isLoadingRecord}>
          {isSaving ? 'Saving...' : 'Save'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

// ---- Inline-Editable Child Tab Grid ----

interface ChildTabGridProps {
  tab: WindowTabDefinition;
  childRecords: Record<string, unknown>[];
  onNavigate?: (tableName: string, recordId: string) => void;
}

/** Renders a single field value inside a table cell for inline editing. */
function ChildFieldCell({
  field,
  value,
  onChange,
}: {
  field: WindowFieldDefinition;
  value: unknown;
  onChange: (val: unknown) => void;
}) {
  const col = field.column;
  const numType = col.type === 'integer' || col.type === 'decimal' || col.type === 'numeric';

  if (col.type === 'boolean') {
    return <Checkbox checked={!!value} onChange={(e) => onChange(e.target.checked)} size="small" />;
  }

  if (col.type === 'date') {
    return (
      <TextField
        type="date"
        value={typeof value === 'string' ? value.slice(0, 10) : ''}
        onChange={(e) => onChange(e.target.value)}
        size="small"
        InputLabelProps={{ shrink: true }}
        sx={{ fontSize: 12, minWidth: 120 }}
      />
    );
  }

  if (col.type === 'enum' && col.enumOptions) {
    const options: string[] = JSON.parse(col.enumOptions);
    return (
      <Select
        value={(value as string) ?? ''}
        onChange={(e) => onChange(e.target.value)}
        size="small"
        sx={{ fontSize: 12, minWidth: 100 }}
      >
        <MenuItem value="">
          <em>None</em>
        </MenuItem>
        {options.map((opt) => (
          <MenuItem key={opt} value={opt}>
            {opt}
          </MenuItem>
        ))}
      </Select>
    );
  }

  return (
    <TextField
      value={String(value ?? '')}
      onChange={(e) => onChange(numType ? e.target.value : e.target.value)}
      size="small"
      type={numType ? 'number' : 'text'}
      inputProps={numType ? { step: col.type === 'integer' ? '1' : '0.01' } : undefined}
      sx={{ fontSize: 12, minWidth: 80 }}
    />
  );
}

function ChildTabGrid({ tab, childRecords, onNavigate }: ChildTabGridProps) {
  const [editMode, setEditMode] = useState(false);
  const [rows, setRows] = useState<Record<string, unknown>[]>(() => [...childRecords]);
  const fields = tab.fields
    .filter((f) => f.isDisplayed !== false)
    .sort((a, b) => a.seqNo - b.seqNo);

  // Sync rows when childRecords prop changes (e.g. after record data reloads)
  useEffect(() => {
    if (!editMode) setRows([...childRecords]);
  }, [childRecords, editMode]);

  const updateCell = (rowIdx: number, colCode: string, val: unknown) => {
    setRows((prev) => {
      const next = prev.map((r, i) => (i === rowIdx ? { ...r, [colCode]: val } : r));
      return next;
    });
  };

  const addRow = () => {
    const newRow: Record<string, unknown> = { _new: true };
    setRows((prev) => [...prev, newRow]);
  };

  const deleteRow = (rowIdx: number) => {
    setRows((prev) => prev.filter((_, i) => i !== rowIdx));
  };

  return (
    <Box sx={{ mb: 2 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, px: 1, mb: 0.5 }}>
        <Typography variant="subtitle2" sx={{ fontWeight: 600 }}>
          {tab.name} ({rows.length})
        </Typography>
        <Button
          size="small"
          variant={editMode ? 'contained' : 'outlined'}
          onClick={() => setEditMode(!editMode)}
          color={editMode ? 'warning' : 'primary'}
          sx={{ ml: 'auto' }}
        >
          {editMode ? 'Done Editing' : 'Quick Update'}
        </Button>
      </Box>
      {rows.length === 0 ? (
        <Typography variant="body2" color="text.secondary" sx={{ px: 1 }}>
          {editMode ? 'No records. Click "+ Add Row" to create one.' : 'No related records found.'}
        </Typography>
      ) : (
        <TableContainer>
          <Table size="small">
            <TableHead>
              <TableRow>
                {fields.map((f) => (
                  <TableCell key={f.column.code} sx={{ fontWeight: 600, fontSize: 12 }}>
                    {f.labelOverride ?? f.column.label}
                  </TableCell>
                ))}
                {editMode && <TableCell sx={{ fontWeight: 600, fontSize: 12 }}>Actions</TableCell>}
              </TableRow>
            </TableHead>
            <TableBody>
              {rows.map((rec, rowIdx) => (
                <TableRow
                  key={(rec.id as string) ?? `new-${rowIdx}`}
                  hover
                  sx={{ cursor: editMode ? 'default' : 'pointer' }}
                  onClick={() => {
                    if (!editMode && rec.id) {
                      onNavigate?.(tab.table?.name ?? '', rec.id as string);
                    }
                  }}
                >
                  {fields.map((f) => (
                    <TableCell key={f.column.code} sx={{ fontSize: 12, p: editMode ? 0.5 : 1 }}>
                      {editMode ? (
                        <ChildFieldCell
                          field={f}
                          value={rec[f.column.code]}
                          onChange={(val) => updateCell(rowIdx, f.column.code, val)}
                        />
                      ) : (
                        String(rec[f.column.code] ?? '')
                      )}
                    </TableCell>
                  ))}
                  {editMode && (
                    <TableCell sx={{ p: 0.5 }}>
                      <Button size="small" color="error" onClick={() => deleteRow(rowIdx)}>
                        Del
                      </Button>
                    </TableCell>
                  )}
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
      {editMode && (
        <Box sx={{ px: 1, mt: 0.5 }}>
          <Button size="small" variant="outlined" onClick={addRow}>
            + Add Row
          </Button>
        </Box>
      )}
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
