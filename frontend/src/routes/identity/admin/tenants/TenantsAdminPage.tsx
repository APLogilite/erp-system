import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { EntityFormDialog, FieldDef } from '@/components/dialogs/EntityFormDialog';
import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface Tenant {
  id: string;
  code: string;
  name: string;
  domain?: string;
  isActive: boolean;
  createdAt: string;
}

const columns: ColumnDef<Tenant>[] = [
  { key: 'code', label: 'Code', width: 120 },
  { key: 'name', label: 'Name' },
  { key: 'domain', label: 'Domain', width: 200 },
  {
    key: 'isActive',
    label: 'Status',
    width: 100,
    render: (t) => (t.isActive ? 'Active' : 'Inactive'),
  },
  {
    key: 'createdAt',
    label: 'Created',
    width: 180,
    render: (t) => new Date(t.createdAt).toLocaleDateString(),
  },
];

const fields: FieldDef[] = [
  { name: 'code', label: 'Code', required: true },
  { name: 'name', label: 'Name', required: true },
  { name: 'domain', label: 'Domain' },
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
];

export function TenantsAdminPage() {
  const queryClient = useQueryClient();
  const { data, isLoading, error, refetch } = useQuery<Tenant[]>({
    queryKey: ['identity', 'tenants'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.tenants);
      return res.data.data || res.data;
    },
  });

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<Tenant | null>(null);

  const handleCreate = () => {
    setEditing(null);
    setDialogOpen(true);
  };

  const handleEdit = (item: Tenant) => {
    setEditing(item);
    setDialogOpen(true);
  };

  const handleDelete = async (item: Tenant) => {
    if (!window.confirm(`Delete tenant "${item.name}"?`)) return;
    try {
      await apiClient.delete(ENDPOINTS.identity.tenant(item.id));
      queryClient.invalidateQueries({ queryKey: ['identity', 'tenants'] });
    } catch {
      /* handled by interceptor */
    }
  };

  const handleSave = async (values: Record<string, string>) => {
    const body = {
      code: values.code,
      name: values.name,
      domain: values.domain || null,
      isActive: values.isActive === 'true',
    };
    if (editing) {
      await apiClient.put(ENDPOINTS.identity.tenant(editing.id), body);
    } else {
      await apiClient.post(ENDPOINTS.identity.tenants, body);
    }
    queryClient.invalidateQueries({ queryKey: ['identity', 'tenants'] });
  };

  return (
    <>
      <AdminListPage
        title="Tenants"
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
        title={editing ? 'Edit Tenant' : 'Create Tenant'}
        fields={fields}
        data={
          editing
            ? {
                code: editing.code,
                name: editing.name,
                domain: editing.domain ?? '',
                isActive: String(editing.isActive),
              }
            : null
        }
        onClose={() => setDialogOpen(false)}
        onSave={handleSave}
      />
    </>
  );
}
