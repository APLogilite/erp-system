import {
  Add,
  ContentCopy,
  Delete,
  Refresh,
  Save,
  Undo,
} from '@mui/icons-material';
import {
  AppBar,
  Box,
  Button,
  IconButton,
  Toolbar,
  Tooltip,
  useMediaQuery,
  useTheme,
} from '@mui/material';
import { useCallback, useState } from 'react';

import { useKeyboardShortcuts } from '../hooks/useKeyboardShortcuts';
import { RecordNavigator } from './RecordNavigator';
import { UnsavedChangesDialog } from './UnsavedChangesDialog';

export interface FormToolbarProps {
  mode: 'list' | 'create' | 'edit' | 'view';
  isDirty: boolean;
  isSaving: boolean;
  recordIndex?: number;
  totalRecords?: number;
  hasPrevious: boolean;
  hasNext: boolean;
  canDelete?: boolean;
  onCreateNew: () => void;
  onSave: () => void;
  onSaveAndNew?: () => void;
  onDiscard: () => void;
  onRefresh: () => void;
  onDelete?: () => void;
  onPrevious: () => void;
  onNext: () => void;
}

export function FormToolbar({
  mode,
  isDirty,
  isSaving,
  recordIndex,
  totalRecords,
  hasPrevious,
  hasNext,
  canDelete = false,
  onCreateNew,
  onSave,
  onSaveAndNew,
  onDiscard,
  onRefresh,
  onDelete,
  onPrevious,
  onNext,
}: FormToolbarProps) {
  const theme = useTheme();
  const isSmall = useMediaQuery(theme.breakpoints.down('sm'));
  const [showDiscardDialog, setShowDiscardDialog] = useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);

  const isList = mode === 'list';
  const isCreate = mode === 'create';

  const handleDiscard = useCallback(() => {
    if (isDirty) {
      setShowDiscardDialog(true);
    } else {
      onDiscard();
    }
  }, [isDirty, onDiscard]);

  const handleDelete = useCallback(() => {
    setShowDeleteDialog(true);
  }, []);

  // Keyboard shortcuts
  useKeyboardShortcuts([
    { key: 's', ctrlKey: true, action: onSave, enabled: () => isSaving || (!isList && !isDirty) },
    { key: 's', ctrlKey: true, shiftKey: true, action: () => onSaveAndNew?.(), enabled: () => !isCreate || isSaving },
    { key: 'Escape', action: handleDiscard, enabled: () => isList },
    { key: 'F5', action: onRefresh },
    { key: 'ArrowLeft', altKey: true, action: onPrevious, enabled: () => !hasPrevious },
    { key: 'ArrowRight', altKey: true, action: onNext, enabled: () => !hasNext },
  ]);

  const btn = (icon: React.ReactNode, label: string, onClick: () => void, disabled = false) =>
    isSmall ? (
      <Tooltip title={label} key={label}>
        <span>
          <IconButton size="small" onClick={onClick} disabled={disabled}>
            {icon}
          </IconButton>
        </span>
      </Tooltip>
    ) : (
      <Button
        key={label}
        size="small"
        startIcon={icon}
        onClick={onClick}
        disabled={disabled}
        sx={{ textTransform: 'none', whiteSpace: 'nowrap' }}
      >
        {label}
      </Button>
    );

  return (
    <>
      <AppBar
        position="sticky"
        color="default"
        elevation={1}
        sx={{ top: 0, zIndex: 10, bgcolor: 'background.paper' }}
      >
        <Toolbar variant="dense" sx={{ gap: 0.5, justifyContent: 'space-between', flexWrap: 'wrap' }}>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, flexWrap: 'wrap' }}>
            {isList && btn(<Add />, 'Create', onCreateNew)}
            {!isList && btn(<Add />, 'New', onCreateNew)}
            {!isList && btn(<Save fontSize="small" />, isSaving ? 'Saving...' : 'Save', onSave, isSaving || !isDirty)}
            {isCreate && onSaveAndNew && btn(<ContentCopy fontSize="small" />, 'Save & New', onSaveAndNew, isSaving)}
            {!isList && btn(<Undo fontSize="small" />, 'Discard', handleDiscard, false)}
            {btn(<Refresh fontSize="small" />, 'Refresh', onRefresh, false)}
            {!isList && onDelete && btn(<Delete fontSize="small" color="error" />, 'Delete', handleDelete, !canDelete)}
          </Box>

          {!isList && (
            <RecordNavigator
              recordIndex={recordIndex}
              totalRecords={totalRecords}
              hasPrevious={hasPrevious}
              hasNext={hasNext}
              onPrevious={onPrevious}
              onNext={onNext}
            />
          )}
        </Toolbar>
      </AppBar>

      <UnsavedChangesDialog
        open={showDiscardDialog}
        onDiscard={() => {
          setShowDiscardDialog(false);
          onDiscard();
        }}
        onCancel={() => setShowDiscardDialog(false)}
      />

      <UnsavedChangesDialog
        open={showDeleteDialog}
        onDiscard={() => {
          setShowDeleteDialog(false);
          onDelete?.();
        }}
        onCancel={() => setShowDeleteDialog(false)}
      />
    </>
  );
}
