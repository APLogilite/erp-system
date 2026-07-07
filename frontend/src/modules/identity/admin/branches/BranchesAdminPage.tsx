import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useMemo, useState } from 'react';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { EntityFormDialog, FieldDef } from '@/components/dialogs/EntityFormDialog';
import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface Branch {
  id: string;
  code: string;
  name: string;
  address?: string;
  phone?: string;
  company?: {
    id: string;
    name: string;
    organization?: { id: string; name: string; tenant?: { id: string; name: string } };
  };
  isActive: boolean;
}

const columns: ColumnDef<Branch>[] = [
  { key: 'code', label: 'Code', width: 120 },
  { key: 'name', label: 'Name' },
  { key: 'address', label: 'Address' },
  { key: 'phone', label: 'Phone', width: 150 },
  {
    key: 'company',
    label: 'Company',
    width: 150,
    render: (b) => b.company?.name ?? '—',
  },
  {
    key: 'organization',
    label: 'Organization',
    width: 150,
    render: (b) => b.company?.organization?.name ?? '—',
  },
  {
    key: 'tenant',
    label: 'Tenant',
    width: 150,
    render: (b) => b.company?.organization?.tenant?.name ?? '—',
  },
  {
    key: 'isActive',
    label: 'Status',
    width: 100,
    render: (b) => (b.isActive ? 'Active' : 'Inactive'),
  },
];

export function BranchesAdminPage() {
  const queryClient = useQueryClient();
  const { data, isLoading, error, refetch } = useQuery<Branch[]>({
    queryKey: ['identity', 'branches'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.branches);
      return res.data.data || res.data;
    },
  });

  const { data: companies } = useQuery<{ id: string; name: string }[]>({
    queryKey: ['identity', 'companies', 'options'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.companies);
      const items = (res.data.data || res.data) as Array<{ id: string; name: string }>;
      return items.map((c) => ({ id: c.id, name: c.name }));
    },
  });

  const fields: FieldDef[] = useMemo(
    () => [
      { name: 'code', label: 'Code', required: true },
      { name: 'name', label: 'Name', required: true },
      { name: 'address', label: 'Address' },
      { name: 'phone', label: 'Phone' },
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
        name: 'companyId',
        label: 'Company',
        type: 'select',
        required: true,
        options: (companies ?? []).map((c) => ({ value: c.id, label: c.name })),
      },
    ],
    [companies]
  );

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<Branch | null>(null);

  const handleCreate = () => {
    setEditing(null);
    setDialogOpen(true);
  };

  const handleEdit = (item: Branch) => {
    setEditing(item);
    setDialogOpen(true);
  };

  const handleDelete = async (item: Branch) => {
    if (!window.confirm(`Delete branch "${item.name}"?`)) return;
    try {
      await apiClient.delete(ENDPOINTS.identity.branch(item.id));
      queryClient.invalidateQueries({ queryKey: ['identity', 'branches'] });
    } catch {
      /* handled by interceptor */
    }
  };

  const handleSave = async (values: Record<string, string>) => {
    const body: Record<string, unknown> = {
      code: values.code,
      name: values.name,
      address: values.address || null,
      phone: values.phone || null,
      isActive: values.isActive === 'true',
      company: values.companyId ? { id: values.companyId } : null,
    };
    if (editing) {
      await apiClient.put(ENDPOINTS.identity.branch(editing.id), body);
    } else {
      await apiClient.post(ENDPOINTS.identity.branches, body);
    }
    queryClient.invalidateQueries({ queryKey: ['identity', 'branches'] });
  };

  return (
    <>
      <AdminListPage
        title="Branches"
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
        title={editing ? 'Edit Branch' : 'Create Branch'}
        fields={fields}
        data={
          editing
            ? {
                code: editing.code,
                name: editing.name,
                address: editing.address ?? '',
                phone: editing.phone ?? '',
                isActive: String(editing.isActive),
                companyId: editing.company?.id ?? '',
              }
            : null
        }
        onClose={() => setDialogOpen(false)}
        onSave={handleSave}
      />
    </>
  );
}
