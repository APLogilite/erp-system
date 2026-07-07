import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { EntityFormDialog, FieldDef } from '@/components/dialogs/EntityFormDialog';
import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface Dept {
  id: string;
  code: string;
  name: string;
  branch?: { id: string; name: string; company?: { id: string; name: string } };
  headName?: string;
  isActive: boolean;
}

const columns: ColumnDef<Dept>[] = [
  { key: 'code', label: 'Code', width: 120 },
  { key: 'name', label: 'Name' },
  {
    key: 'branch',
    label: 'Branch',
    width: 150,
    render: (d) => d.branch?.name ?? '—',
  },
  {
    key: 'company',
    label: 'Company',
    width: 150,
    render: (d) => d.branch?.company?.name ?? '—',
  },
  { key: 'headName', label: 'Head', width: 150 },
  {
    key: 'isActive',
    label: 'Status',
    width: 100,
    render: (d) => (d.isActive ? 'Active' : 'Inactive'),
  },
];

export function DepartmentsAdminPage() {
  const queryClient = useQueryClient();
  const { data, isLoading, error, refetch } = useQuery<Dept[]>({
    queryKey: ['identity', 'departments'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.departments);
      return res.data.data || res.data;
    },
  });

  const { data: branches } = useQuery<{ id: string; name: string }[]>({
    queryKey: ['identity', 'branches', 'options'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.branches);
      const items = (res.data.data || res.data) as Array<{ id: string; name: string }>;
      return items.map((b) => ({ id: b.id, name: b.name }));
    },
  });

  const fields: FieldDef[] = [
    { name: 'code', label: 'Code', required: true },
    { name: 'name', label: 'Name', required: true },
    { name: 'headName', label: 'Head' },
    {
      name: 'isActive',
      label: 'Status',
      type: 'select',
      initialValue: 'true',
      options: [
        { value: 'true', label: 'Active' },
        { value: 'false', label: 'Inactive' },
      ],
    },
    {
      name: 'branchId',
      label: 'Branch',
      type: 'select',
      required: true,
      options: (branches ?? []).map((b) => ({ value: b.id, label: b.name })),
    },
  ];

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<Dept | null>(null);

  const handleCreate = () => {
    setEditing(null);
    setDialogOpen(true);
  };

  const handleEdit = (item: Dept) => {
    setEditing(item);
    setDialogOpen(true);
  };

  const handleDelete = async (item: Dept) => {
    if (!window.confirm(`Delete department "${item.name}"?`)) return;
    try {
      await apiClient.delete(ENDPOINTS.identity.department(item.id));
      queryClient.invalidateQueries({ queryKey: ['identity', 'departments'] });
    } catch {
      /* handled by interceptor */
    }
  };

  const handleSave = async (values: Record<string, string>) => {
    const body: Record<string, unknown> = {
      code: values.code,
      name: values.name,
      headName: values.headName || null,
      isActive: values.isActive === 'true',
      branch: values.branchId ? { id: values.branchId } : null,
    };
    if (editing) {
      await apiClient.put(ENDPOINTS.identity.department(editing.id), body);
    } else {
      await apiClient.post(ENDPOINTS.identity.departments, body);
    }
    queryClient.invalidateQueries({ queryKey: ['identity', 'departments'] });
  };

  return (
    <>
      <AdminListPage
        title="Departments"
        columns={columns}
        data={data}
        isLoading={isLoading}
        error={error as Error | null}
        onRefresh={refetch}
        onCreate={handleCreate}
        onEdit={handleEdit}
        onDelete={handleDelete}
      />
      <EntityFormDialog
        open={dialogOpen}
        title={editing ? 'Edit Department' : 'Create Department'}
        fields={fields}
        data={
          editing
            ? {
                code: editing.code,
                name: editing.name,
                headName: editing.headName ?? '',
                isActive: String(editing.isActive),
                branchId: editing.branch?.id ?? '',
              }
            : null
        }
        onClose={() => setDialogOpen(false)}
        onSave={handleSave}
      />
    </>
  );
}
