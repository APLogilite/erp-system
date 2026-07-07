import { Chip } from '@mui/material';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { EntityFormDialog, FieldDef } from '@/components/dialogs/EntityFormDialog';
import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface Org {
  id: string;
  code: string;
  name: string;
  tenant?: { id: string; name: string };
  level: number;
  path: string;
  isActive: boolean;
  createdAt: string;
}

const columns: ColumnDef<Org>[] = [
  { key: 'code', label: 'Code', width: 120 },
  { key: 'name', label: 'Name' },
  {
    key: 'tenant',
    label: 'Tenant',
    width: 150,
    render: (o) => o.tenant?.name ?? '—',
  },
  {
    key: 'level',
    label: 'Level',
    width: 80,
    render: (o) => <Chip label={o.level} size="small" variant="outlined" />,
  },
  { key: 'path', label: 'Path' },
  {
    key: 'isActive',
    label: 'Status',
    width: 100,
    render: (o) => (o.isActive ? 'Active' : 'Inactive'),
  },
];

export function OrganizationsAdminPage() {
  const queryClient = useQueryClient();
  const { data, isLoading, error, refetch } = useQuery<Org[]>({
    queryKey: ['identity', 'organizations'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.organizations);
      return res.data.data || res.data;
    },
  });

  const { data: tenants } = useQuery<{ id: string; name: string }[]>({
    queryKey: ['identity', 'tenants', 'options'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.tenants);
      const items = (res.data.data || res.data) as Array<{ id: string; name: string }>;
      return items.map((t) => ({ id: t.id, name: t.name }));
    },
  });

  const fields: FieldDef[] = [
    { name: 'code', label: 'Code', required: true },
    { name: 'name', label: 'Name', required: true },
    { name: 'path', label: 'Path', required: true },
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
      name: 'tenantId',
      label: 'Tenant',
      type: 'select',
      required: true,
      options: (tenants ?? []).map((t) => ({ value: t.id, label: t.name })),
    },
  ];

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<Org | null>(null);

  const handleCreate = () => {
    setEditing(null);
    setDialogOpen(true);
  };

  const handleEdit = (item: Org) => {
    setEditing(item);
    setDialogOpen(true);
  };

  const handleDelete = async (item: Org) => {
    if (!window.confirm(`Delete organization "${item.name}"?`)) return;
    try {
      await apiClient.delete(ENDPOINTS.identity.organization(item.id));
      queryClient.invalidateQueries({ queryKey: ['identity', 'organizations'] });
    } catch {
      /* handled by interceptor */
    }
  };

  const handleSave = async (values: Record<string, string>) => {
    const body: Record<string, unknown> = {
      code: values.code,
      name: values.name,
      path: values.path,
      isActive: values.isActive === 'true',
      tenant: values.tenantId ? { id: values.tenantId } : null,
    };
    if (editing) {
      await apiClient.put(ENDPOINTS.identity.organization(editing.id), body);
    } else {
      await apiClient.post(ENDPOINTS.identity.organizations, body);
    }
    queryClient.invalidateQueries({ queryKey: ['identity', 'organizations'] });
  };

  return (
    <>
      <AdminListPage
        title="Organizations"
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
        title={editing ? 'Edit Organization' : 'Create Organization'}
        fields={fields}
        data={
          editing
            ? {
                code: editing.code,
                name: editing.name,
                path: editing.path,
                isActive: String(editing.isActive),
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
