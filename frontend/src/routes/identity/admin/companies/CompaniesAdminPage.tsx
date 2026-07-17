import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useMemo, useState } from 'react';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { EntityFormDialog, FieldDef } from '@/components/dialogs/EntityFormDialog';
import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface Company {
  id: string;
  code: string;
  name: string;
  taxId?: string;
  organization?: { id: string; name: string; tenant?: { id: string; name: string } };
  isActive: boolean;
  createdAt: string;
}

const columns: ColumnDef<Company>[] = [
  { key: 'code', label: 'Code', width: 120 },
  { key: 'name', label: 'Name' },
  { key: 'taxId', label: 'Tax ID', width: 150 },
  {
    key: 'organization',
    label: 'Organization',
    width: 150,
    render: (c) => c.organization?.name ?? '—',
  },
  {
    key: 'tenant',
    label: 'Tenant',
    width: 150,
    render: (c) => c.organization?.tenant?.name ?? '—',
  },
  {
    key: 'isActive',
    label: 'Status',
    width: 100,
    render: (c) => (c.isActive ? 'Active' : 'Inactive'),
  },
];

export function CompaniesAdminPage() {
  const queryClient = useQueryClient();
  const { data, isLoading, error, refetch } = useQuery<Company[]>({
    queryKey: ['identity', 'companies'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.companies);
      return res.data.data || res.data;
    },
  });

  const { data: orgs } = useQuery<{ id: string; name: string }[]>({
    queryKey: ['identity', 'organizations', 'options'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.organizations);
      const items = (res.data.data || res.data) as Array<{ id: string; name: string }>;
      return items.map((o) => ({ id: o.id, name: o.name }));
    },
  });

  const fields: FieldDef[] = useMemo(
    () => [
      { name: 'code', label: 'Code', required: true },
      { name: 'name', label: 'Name', required: true },
      { name: 'taxId', label: 'Tax ID' },
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
        name: 'organizationId',
        label: 'Organization',
        type: 'select',
        required: true,
        options: (orgs ?? []).map((o) => ({ value: o.id, label: o.name })),
      },
    ],
    [orgs]
  );

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<Company | null>(null);

  const handleCreate = () => {
    setEditing(null);
    setDialogOpen(true);
  };

  const handleEdit = (item: Company) => {
    setEditing(item);
    setDialogOpen(true);
  };

  const handleDelete = async (item: Company) => {
    if (!window.confirm(`Delete company "${item.name}"?`)) return;
    try {
      await apiClient.delete(ENDPOINTS.identity.company(item.id));
      queryClient.invalidateQueries({ queryKey: ['identity', 'companies'] });
    } catch {
      /* handled by interceptor */
    }
  };

  const handleSave = async (values: Record<string, string>) => {
    const body: Record<string, unknown> = {
      code: values.code,
      name: values.name,
      taxId: values.taxId || null,
      isActive: values.isActive === 'true',
      organization: values.organizationId ? { id: values.organizationId } : null,
    };
    if (editing) {
      await apiClient.put(ENDPOINTS.identity.company(editing.id), body);
    } else {
      await apiClient.post(ENDPOINTS.identity.companies, body);
    }
    queryClient.invalidateQueries({ queryKey: ['identity', 'companies'] });
  };

  return (
    <>
      <AdminListPage
        title="Companies"
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
        title={editing ? 'Edit Company' : 'Create Company'}
        fields={fields}
        data={
          editing
            ? {
                code: editing.code,
                name: editing.name,
                taxId: editing.taxId ?? '',
                isActive: String(editing.isActive),
                organizationId: editing.organization?.id ?? '',
              }
            : null
        }
        onClose={() => setDialogOpen(false)}
        onSave={handleSave}
      />
    </>
  );
}
