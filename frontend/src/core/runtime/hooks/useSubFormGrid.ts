import { useState, useCallback } from 'react';
import type { RecordEntry, SubFormDefinition } from './useForm.types';
import type { ColumnInfo } from '../components/InlineEditableGrid';

export interface SubFormGridState {
  records: RecordEntry[];
  columns: ColumnInfo[];
  pendingAdds: Omit<RecordEntry, 'id'>[];
  pendingUpdates: Map<string, Partial<RecordEntry>>;
  pendingDeletes: Set<string>;
}

export function useSubFormGrid(
  _subForm: SubFormDefinition,
  initialRecords: RecordEntry[],
  fields: { columnCode: string; label: string }[],
) {
  const [records, setRecords] = useState<RecordEntry[]>(initialRecords);
  const [pendingAdds, setPendingAdds] = useState<Omit<RecordEntry, 'id'>[]>([]);
  const [pendingUpdates, setPendingUpdates] = useState<Map<string, Partial<RecordEntry>>>(new Map());
  const [pendingDeletes, setPendingDeletes] = useState<Set<string>>(new Set());

  const columns: ColumnInfo[] = fields.map((f) => ({
    key: f.columnCode,
    label: f.label,
    editable: true,
  }));

  const handleAdd = useCallback((record: Omit<RecordEntry, 'id'>) => {
    const tempId = `new_${Date.now()}`;
    const newRecord: RecordEntry = { id: tempId, ...record } as RecordEntry;
    setRecords((prev) => [...prev, newRecord]);
    setPendingAdds((prev) => [...prev, record]);
  }, []);

  const handleUpdate = useCallback((recordId: string, data: Partial<RecordEntry>) => {
    setRecords((prev) => prev.map((r) => (r.id === recordId ? { ...r, ...data } : r)));

    if (recordId.startsWith('new_')) {
      setPendingAdds((prev) => prev.map((a, i) => {
        // Match by position
        const tempIds = records.filter((r) => r.id.startsWith('new_')).map((r) => r.id);
        const idx = tempIds.indexOf(recordId);
        return idx === i ? { ...a, ...data } as unknown as Omit<RecordEntry, 'id'> : a;
      }));
    } else {
      setPendingUpdates((prev) => {
        const next = new Map(prev);
        const existing = next.get(recordId) || {};
        next.set(recordId, { ...existing, ...data });
        return next;
      });
    }
  }, [records]);

  const handleDelete = useCallback((recordId: string) => {
    setRecords((prev) => prev.filter((r) => r.id !== recordId));

    if (recordId.startsWith('new_')) {
      setPendingAdds((prev) => {
        const tempIds = records.filter((r) => r.id.startsWith('new_')).map((r) => r.id);
        const idx = tempIds.indexOf(recordId);
        return prev.filter((_, i) => i !== idx);
      });
    } else {
      setPendingDeletes((prev) => new Set(prev).add(recordId));
    }
  }, [records]);

  const getChanges = useCallback(() => ({
    adds: pendingAdds,
    updates: Object.fromEntries(pendingUpdates),
    deletes: Array.from(pendingDeletes),
  }), [pendingAdds, pendingUpdates, pendingDeletes]);

  return {
    records,
    columns,
    handleAdd,
    handleUpdate,
    handleDelete,
    getChanges,
    pendingAdds: pendingAdds.length > 0,
    pendingUpdates: pendingUpdates.size > 0,
    pendingDeletes: pendingDeletes.size > 0,
    isDirty: pendingAdds.length > 0 || pendingUpdates.size > 0 || pendingDeletes.size > 0,
  };
}
