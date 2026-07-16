import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
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
  Grid,
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
import React, { useCallback, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

import { PageContainer } from '@/components/layouts/PageContainer';
import {
  fetchWindowDefinition,
  fetchWindowRecords,
  fetchWindowRecord,
  fetchTabRecord,
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

// ---- Drill Level (breadcrumb entry) ----
interface DrillLevel {
  tab: WindowTabDefinition;
  recordId: string;
  title: string;
  recordData: Record<string, unknown>;
}

// ---- Record Dialog ----
// Supports drill-down navigation through the tab hierarchy.
// Child records are shown as expandable Accordion panels below the form.
// Clicking a child record drills down (pushes breadcrumb stack).

interface RecordDialogProps {
  open: boolean;
  windowName: string;
  windowDef: WindowDefinition;
  recordId?: string;
  onClose: () => void;
  onDrillDown?: (tab: WindowTabDefinition, recordId: string) => void;
}

function RecordDialog({ open, windowName, windowDef, recordId, onClose }: RecordDialogProps) {
  const queryClient = useQueryClient();
  const mainTab = windowDef.tabs.find((t) => !t.parentColumn);
  const [formData, setFormData] = useState<Record<string, unknown>>({});
  const [saveError, setSaveError] = useState<string | null>(null);

  // Drill-down state
  const [drillStack, setDrillStack] = useState<DrillLevel[]>([]);
  const [expandedPanels, setExpandedPanels] = useState<Set<string>>(new Set());
  const [hasToggledPanel, setHasToggledPanel] = useState(false);

  // Determine which tab provides the form fields for the current level
  const currentLevelTab: WindowTabDefinition =
    drillStack.length > 0 ? drillStack[drillStack.length - 1].tab : mainTab!;

  // Collect unique relation table names for lookup dropdowns
  const lookupTables = [
    ...new Set(
      (windowDef.tabs.flatMap((t) => t.fields) ?? [])
        .filter((f) => f.column.relationTable)
        .map((f) => f.column.relationTable!)
    ),
  ];

  // Build lookup queries, resolving filter_where_clause if defined per field
  const lookupConfigs = lookupTables.map((tableName) => {
    // Find fields that reference this table to check for filter_where_clause
    const refFields = windowDef.tabs.flatMap((t) => t.fields).filter((f) => f.column.relationTable === tableName);
    let filterField: string | undefined;
    let filterValue: string | undefined;
    for (const ff of refFields) {
      if (ff.column.filterWhereClause) {
        // Resolve @parentTableId@ from the parent tab context (if drilled, use parent tab's table)
        let resolved = ff.column.filterWhereClause;
        if (resolved.includes('@parentTableId@')) {
          const parentMeta = isDrilled && drillStack.length >= 1
            ? drillStack[drillStack.length - 1].tab.table.id
            : mainTab?.table.id;
          if (parentMeta) resolved = resolved.replace('@parentTableId@', parentMeta);
        }
        const eqIdx = resolved.indexOf('=');
        if (eqIdx > 0) {
          filterField = resolved.substring(0, eqIdx).trim();
          filterValue = resolved.substring(eqIdx + 1).trim();
        }
        break;
      }
    }
    return { tableName, filterField, filterValue };
  });

  const lookupResults = useQueries({
    queries: lookupConfigs.map((cfg) => ({
      queryKey: ['lookup', cfg.tableName, cfg.filterField, cfg.filterValue],
      queryFn: () => fetchLookupRecords(cfg.tableName, cfg.filterField, cfg.filterValue),
      staleTime: 30000,
      gcTime: 60000,
    })),
  });

  const lookupQueries: Record<string, Record<string, unknown>[]> = {};
  lookupTables.forEach((tableName, idx) => {
    lookupQueries[tableName] = lookupResults[idx]?.data ?? [];
  });

  // Determine if we're at root level or drilled into a child record
  const isDrilled = drillStack.length > 0;
  const currentDrillLevel = isDrilled ? drillStack[drillStack.length - 1] : null;

  // Fetch the current level's record data (with grandchildren if applicable)
  const currentRecordId = isDrilled ? drillStack[drillStack.length - 1].recordId : recordId;

  const { data: recordData, isLoading: isLoadingRecord, error: recordError } = useQuery({
    queryKey: ['window-record', windowName, drillStack.length, currentLevelTab.id, currentRecordId],
    queryFn: () => {
      if (isDrilled) {
        // Drilled: always use fetchTabRecord which targets the correct tab's table,
        // even if there are no grandchildren (empty childTabIds is fine)
        const childTabIds = findChildTabs(windowDef.tabs, currentLevelTab).map((t) => t.id);
        return fetchTabRecord(windowName, currentLevelTab.id, currentRecordId!, childTabIds);
      }
      // Root level: fetch record with children from main tab
      return fetchWindowRecord(windowName, currentRecordId!);
    },
    enabled: !!currentRecordId,
  });

  // Extract child records for the current level (grandchildren when drilled)
  const childRecordsMap = recordData
    ? ((recordData as { childRecords?: Record<string, Record<string, unknown>[]> }).childRecords ?? {})
    : {};

  // For the form data: use drill stack data when drilled (preserves user edits on refetch)
  // At root level: use the record from the API response
  const effectiveFormRecord = isDrilled && currentDrillLevel
    ? currentDrillLevel.recordData
    : recordData
        ? ((recordData as { record?: Record<string, unknown> }).record ?? recordData)
        : undefined;

  // Reset drill stack when opening a different record
  useEffect(() => {
    setDrillStack([]);
    setExpandedPanels(new Set());
  }, [recordId, windowName]);

  // Reset form data when drill level changes (before new data arrives)
  useEffect(() => {
    setFormData({});
  }, [drillStack.length, currentLevelTab.id]);

  // Track which record+tab the form is initialized for (to avoid overwriting user edits on refetch)
  const formKey = `${currentRecordId ?? ''}-${currentLevelTab?.id ?? ''}`;
  const [initializedKey, setInitializedKey] = useState<string>('');

  // Initialize form data when opening a new record or changing drill level (NOT on refetches)
  useEffect(() => {
    if (effectiveFormRecord && formKey !== initializedKey) {
      setFormData(effectiveFormRecord as Record<string, unknown>);
      setInitializedKey(formKey);
    }
  }, [effectiveFormRecord, formKey, initializedKey]);

  // Helper to invalidate ALL queries for this window (list + record + definition)
  const invalidateWindowCache = useCallback(() => {
    queryClient.invalidateQueries({ queryKey: ['window-records', windowName] });
    queryClient.invalidateQueries({ queryKey: ['window-record', windowName] });
    queryClient.invalidateQueries({ queryKey: ['window-definition', windowName] });
  }, [queryClient, windowName]);

  // Mutations
  const createMutation = useMutation({
    mutationFn: (data: Record<string, unknown>) => {
      // When drilled down, pass tab ID and parent record ID to auto-set parent FK
      const tabId = isDrilled ? currentLevelTab.id : undefined;
      const parentRecordId = isDrilled && drillStack.length >= 2
        ? drillStack[drillStack.length - 2].recordId
        : undefined;
      return createWindowRecord(windowName, data, tabId, parentRecordId);
    },
    onSuccess: () => {
      invalidateWindowCache();
      onClose();
    },
    onError: (err: Error) => setSaveError(err.message),
  });

  const updateMutation = useMutation({
    mutationFn: (data: Record<string, unknown>) => {
      const tabId = isDrilled ? currentLevelTab.id : undefined;
      return updateWindowRecord(windowName, currentRecordId!, data, tabId);
    },
    onSuccess: () => {
      invalidateWindowCache();
      setInitializedKey(''); // Force re-initialize on next open
      onClose();
    },
    onError: (err: Error) => setSaveError(err.message),
  });

  const handleSave = useCallback(() => {
    const parsed = { ...formData };
    for (const field of currentLevelTab?.fields ?? []) {
      const val = parsed[field.column.code];
      if (val === '' || val === undefined || val === null) continue;
      if (field.column.type === 'integer') parsed[field.column.code] = parseInt(val as string, 10);
      else if (field.column.type === 'decimal' || field.column.type === 'numeric')
        parsed[field.column.code] = parseFloat(val as string);
    }
    if (currentRecordId) updateMutation.mutate(parsed);
    else createMutation.mutate(parsed);
  }, [currentRecordId, formData, currentLevelTab, createMutation, updateMutation]);

  const handleFieldChange = useCallback((fieldCode: string, value: unknown) => {
    setFormData((prev) => ({ ...prev, [fieldCode]: value }));
  }, []);

  const isSaving = createMutation.isPending || updateMutation.isPending;

  // Find child tabs of a given tab (for accordion panels)
  function findChildTabs(
    allTabs: WindowTabDefinition[],
    parentTab: WindowTabDefinition
  ): WindowTabDefinition[] {
    const parentTable = parentTab.table.name; // e.g., 'sys_window'
    return allTabs.filter((t) => {
      if (!t.parentColumn || !t.parentColumn.endsWith('_id')) return false;
      const colStub = t.parentColumn.slice(0, -3); // 'window_id' → 'window'
      // Match: parentColumn references parent's table name
      // 'window_id' → colStub='window' → parentTable ends with '_window'
      return parentTable.endsWith('_' + colStub);
    });
  }

  const currentChildTabs = currentLevelTab ? findChildTabs(windowDef.tabs, currentLevelTab) : [];

  // Drill down: user clicked a row in a child grid
  const handleDrillDown = (childTab: WindowTabDefinition, childRecordId: string) => {
    const rd = recordData as Record<string, unknown>;
    const children = ((rd.childRecords as Record<string, unknown[]>) ?? {})[childTab.name] ?? [];
    const childRecord = (children as Array<Record<string, unknown>>).find(
      (r) => r.id === childRecordId
    );
    setDrillStack((prev) => [
      ...prev,
      {
        tab: childTab,
        recordId: childRecordId,
        title: childTab.name,
        recordData: childRecord ?? ({} as Record<string, unknown>),
      },
    ]);
    setExpandedPanels(new Set());
  };

  // Go back to a specific breadcrumb level
  const goToLevel = (level: number) => {
    setDrillStack((prev) => prev.slice(0, level));
    setExpandedPanels(new Set());
  };

  // Toggle accordion panel (stops auto-expanding first panel after user interacts)
  const togglePanel = (panelId: string) => {
    setHasToggledPanel(true);
    setExpandedPanels((prev) => {
      const next = new Set(prev);
      if (next.has(panelId)) next.delete(panelId);
      else next.add(panelId);
      return next;
    });
  };

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

  const fields = getDisplayedFields(currentLevelTab);

  // Breadcrumb: show each level as "TabName (DisplayValue)"
  const getDisplayVal = (rec: Record<string, unknown> | undefined): string =>
    ((rec?._display as string) ?? (rec?.name as string) ?? (rec?.code as string) ?? '');
  const breadcrumbParts = [
    // Root: window name (with parent record display if editing)
    recordId
      ? windowDef.window.name + (effectiveFormRecord ? ' (' + getDisplayVal(effectiveFormRecord as Record<string, unknown>) + ')' : '')
      : windowDef.window.name,
    // Drill levels: tab name (record display value)
    ...drillStack.map((l) => l.tab.name + ' (' + getDisplayVal(l.recordData as Record<string, unknown>) + ')'),
  ];
  const currentTitle = isDrilled
    ? windowDef.window.name
    : (recordId ? windowDef.window.name : 'New ' + (currentLevelTab?.name ?? 'Record'));

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle sx={{ pb: 1 }}>
        {/* Breadcrumb navigation path */}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mb: 0.5 }}>
          {breadcrumbParts.map((part, i) => (
            <React.Fragment key={i}>
              {i > 0 && <Typography sx={{ fontSize: 13, color: 'text.secondary' }}>&gt;</Typography>}
              <Button
                size="small"
                sx={{ textTransform: 'none', minWidth: 0, px: 0.5, fontSize: 13, color: i < breadcrumbParts.length - 1 ? 'text.secondary' : 'text.primary' }}
                onClick={() => {
                  if (i === 0) { goToLevel(0); setDrillStack([]); }
                  else goToLevel(i - 1);
                }}
              >
                {part}
              </Button>
            </React.Fragment>
          ))}
        </Box>
        {currentTitle}
      </DialogTitle>

      <DialogContent sx={{ mt: 1 }}>
        {saveError && (
          <Typography color="error" sx={{ mb: 2, p: 1, bgcolor: 'error.light', borderRadius: 1 }}>
            {saveError}
          </Typography>
        )}
        {recordError && (
          <Typography color="error" sx={{ mb: 2, p: 1, bgcolor: 'error.light', borderRadius: 1 }}>
            Failed to load record: {(recordError as Error).message}
          </Typography>
        )}

        {/* Form fields for the current level */}
        {isLoadingRecord && currentRecordId ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
            <CircularProgress />
          </Box>
        ) : (
          <Box sx={{ pt: 1 }}>
            {fields.map((field) => {
              const value = formData[field.column.code] ?? '';
              const label = field.label;
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
                  const opts: string[] = field.column.enumOptions
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
                        {opts.map((o) => (
                          <MenuItem key={o} value={o}>
                            {o}
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
                  const lo = lookupQueries[field.column.relationTable!] ?? [];
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
                        {lo.map((o) => (
                          <MenuItem key={o.id as string} value={o.id as string}>
                            {(o._display as string) ?? (o.id as string)}
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
        )}

        {/* Accordion panels for child records — each takes full width (iDempiere style) */}
        {currentRecordId && currentChildTabs.length > 0 && (
          <Box sx={{ mt: 2 }}>
            <Grid container spacing={1}>
              {currentChildTabs.map((ct) => {
                const childRecords = childRecordsMap[ct.name] ?? [];
                const panelId = ct.id;
                return (
                  <Grid item xs={12} key={panelId}>
                    <Accordion
                      expanded={
                        expandedPanels.has(panelId) ||
                        (!hasToggledPanel && expandedPanels.size === 0 && currentChildTabs.indexOf(ct) === 0)
                      }
                      onChange={() => togglePanel(panelId)}
                    >
                      <AccordionSummary>
                        <Typography variant="subtitle2" sx={{ fontWeight: 600, fontSize: 13 }}>
                          {expandedPanels.has(panelId) ? '▼' : '▶'} {ct.name} ({childRecords.length}
                          )
                        </Typography>
                      </AccordionSummary>
                      <AccordionDetails sx={{ p: 1 }}>
                        <ChildTabGrid
                          tab={ct}
                          childRecords={childRecords}
                          onRowClick={(rid: string) => handleDrillDown(ct, rid)}
                        />
                      </AccordionDetails>
                    </Accordion>
                  </Grid>
                );
              })}
            </Grid>
          </Box>
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
  onRowClick?: (recordId: string) => void;
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

function ChildTabGrid({ tab, childRecords, onRowClick }: ChildTabGridProps) {
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
                    {f.label}
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
                      onRowClick?.(rec.id as string);
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
                        (rec[f.column.code + '_display'] as string) ?? String(rec[f.column.code] ?? '')
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

  // Determine current top-level tab
  const topTabs = windowDef.tabs.filter((t) => !t.parentColumn).sort((a, b) => a.seqNo - b.seqNo);
  const currentTab = topTabs[activeTab] ?? topTabs[0];

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
        <Button
          variant="outlined"
          onClick={() => {
            queryClient.invalidateQueries({ queryKey: ['window-definition', windowName] });
            queryClient.invalidateQueries({ queryKey: ['window-records', windowName] });
            queryClient.invalidateQueries({ queryKey: ['window-record', windowName] });
          }}
        >
          ↻ Refresh
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
                      {f.label}
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
                    {fields.map((f) => {
                      // For many2one fields, show display name instead of UUID
                      const displayVal = record[f.column.code + '_display'] as string | undefined;
                      const rawVal = record[f.column.code];
                      return (
                        <TableCell key={f.column.code}>
                          {displayVal ?? String(rawVal ?? '')}
                        </TableCell>
                      );
                    })}
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
