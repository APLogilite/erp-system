import { Box, Tab, Tabs } from '@mui/material';
import { useState } from 'react';

import type { RecordEntry, SubFormDefinition } from '../hooks/useForm.types';
import { InlineEditableGrid } from './InlineEditableGrid';

interface Props {
  subForms: SubFormDefinition[];
  subFormRecords: Record<string, RecordEntry[]>;
  onDrillDown?: (formCode: string, recordId: string) => void;
}

export function SubFormTabPanel({ subForms, subFormRecords, onDrillDown }: Props) {
  const [activeTab, setActiveTab] = useState(0);

  if (!subForms || subForms.length === 0) return null;

  const handleTabChange = (_: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue);
  };

  const currentSubForm = subForms[activeTab];
  const records = subFormRecords?.[currentSubForm?.relationCode] ?? [];

  // Derive columns from the sub-form's known fields
  const sampleRecord = records[0];
  const columnKeys = sampleRecord
    ? Object.keys(sampleRecord).filter((k) => k !== 'id' && typeof sampleRecord[k] !== 'object')
    : [];

  return (
    <Box sx={{ mt: 3, borderTop: 1, borderColor: 'divider', pt: 2 }}>
      <Tabs value={activeTab} onChange={handleTabChange}
        variant="scrollable" scrollButtons="auto" sx={{ mb: 2 }}>
        {subForms.map((sf, idx) => {
          const count = subFormRecords?.[sf.relationCode]?.length ?? 0;
          return (
            <Tab
              key={sf.id}
              label={`${sf.label} (${count})`}
              value={idx}
            />
          );
        })}
      </Tabs>

      {currentSubForm && (
        <InlineEditableGrid
          columns={columnKeys.map((k) => ({ key: k, label: k.replace(/_/g, ' '), editable: true }))}
          records={records}
          onAdd={(rec) => {
            // Collect and submit with parent
            console.debug('Sub-form add:', currentSubForm.relationCode, rec);
          }}
          onUpdate={(id, data) => {
            console.debug('Sub-form update:', currentSubForm.relationCode, id, data);
          }}
          onDelete={(id) => {
            console.debug('Sub-form delete:', currentSubForm.relationCode, id);
          }}
          onRowClick={onDrillDown
            ? (recordId) => onDrillDown(currentSubForm.childFormCode, recordId)
            : undefined}
        />
      )}
    </Box>
  );
}
