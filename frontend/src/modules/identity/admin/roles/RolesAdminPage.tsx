import { Chip } from '@mui/material';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useMemo, useState } from 'react';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { EntityFormDialog, FieldDef } from '@/components/dialogs/EntityFormDialog';
import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface Role {
  id: string;
  code: string;
  name: string;
  description?: string;
  isSystem: boolean;
  tenant?: { id: string; name: string };
  createdAt: string;
}

const columns: ColumnDef<Role>[] = [
  { key: 'code', label: 'Code', width: 150 },
  { key: 'name', label: 'Name' },
  { key: 'description', label: 'Description' },
  {
    key: 'isSystem',
    label: 'Type',
    width: 100,
    render: (r) =>
      r.isSystem ? (
        <Chip label="System" size="small" color="info" variant="outlined" />
      ) : (
        <Chip label="Custom" size="small" variant="outlined" />
      ),
  },
  {
    key: 'tenant',
    label: 'Tenant',
    width: 150,
    render: (r) => r.tenant?.name ?? '—',
  },
];

export function RolesAdminPage() {
  const queryClient = useQueryClient();
  const { data, isLoading, error, refetch } = useQuery<Role[]>({
    queryKey: ['identity', 'roles'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.roles);
      return res.data.data || res.data;
    },
  });

  const { data: tenants } = useQuery<{ id: string; name: string }[]>({
    queryKey: ['identity', 'tenants', 'options'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.tenants);
      const list: Record<string, string>[] = res.data.data || res.data;
      return list.map((t) => ({ id: t.id, name: t.name }));
    },
  });

  const fields: FieldDef[] = useMemo(
    () => [
      { name: 'code', label: 'Code', required: true },
      { name: 'name', label: 'Name', required: true },
      { name: 'description', label: 'Description' },
      {
        name: 'isSystem',
        label: 'System Role',
        type: 'select',
        initialValue: 'false',
        options: [
          { value: 'true', label: 'System' },
          { value: 'false', label: 'Custom' },
        ],
      },
      {
        name: 'tenantId',
        label: 'Tenant',
        type: 'select',
        initialValue: '',
        allowNone: true,
        options: (tenants ?? []).map((t) => ({ value: t.id, label: t.name })),
      },
    ],
    [tenants]
  );

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<Role | null>(null);

  const handleCreate = () => {
    setEditing(null);
    setDialogOpen(true);
  };

  const handleEdit = (item: Role) => {
    setEditing(item);
    setDialogOpen(true);
  };

  const handleDelete = async (item: Role) => {
    if (!window.confirm(`Delete role "${item.name}"?`)) return;
    try {
      await apiClient.delete(ENDPOINTS.identity.role(item.id));
      queryClient.invalidateQueries({ queryKey: ['identity', 'roles'] });
    } catch {
      /* handled by interceptor */
    }
  };

  const handleSave = async (values: Record<string, string>) => {
    const body: Record<string, unknown> = {
      code: values.code,
      name: values.name,
      description: values.description || null,
      isSystem: values.isSystem === 'true',
      tenant: values.tenantId ? { id: values.tenantId } : null,
    };
    if (editing) {
      await apiClient.put(ENDPOINTS.identity.role(editing.id), body);
    } else {
      await apiClient.post(ENDPOINTS.identity.roles, body);
    }
    queryClient.invalidateQueries({ queryKey: ['identity', 'roles'] });
  };

  return (
    <>
      <AdminListPage
        title="Roles"
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
        title={editing ? 'Edit Role' : 'Create Role'}
        fields={fields}
        data={
          editing
            ? {
                code: editing.code,
                name: editing.name,
                description: editing.description ?? '',
                isSystem: String(editing.isSystem),
                tenantId: editing.tenant?.id ?? '',
              }
            : null
        }
        onClose={() => setDialogOpen(false)}
        onSave={handleSave}
      />
    </>
  );
}
