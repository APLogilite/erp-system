import { Info } from '@mui/icons-material';
import {
  Box,
  Chip,
  CircularProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from '@mui/material';
import { useCallback, useEffect, useState } from 'react';

import { ErrorState } from '@/components/ui/ErrorState';
import { apiClient } from '@/core/api/client';

// ---- Types ----

interface Tenant {
  id: string;
  code: string;
  name: string;
}

interface Role {
  id: string;
  code: string;
  name: string;
  tenantId?: string;
}

interface TenantRoleEntry {
  formId: string;
  tenantId: string;
  roleIds: string[];
}

interface TenantAccessRow {
  tenantId: string;
  tenantName: string;
  tenantCode: string;
  configured: boolean;
  roleNames: string[];
}

interface Props {
  formId: string;
}

// ---- Component ----

export function GlobalFormTenantAccessTable({ formId }: Props) {
  const [rows, setRows] = useState<TenantAccessRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [tenantsRes, rolesRes, assignmentsRes] = await Promise.all([
        apiClient.get('/identity/tenants'),
        apiClient.get('/identity/roles'),
        apiClient.get(`/metadata/forms/${formId}/global-tenant-roles`),
      ]);

      const tenants: Tenant[] = tenantsRes.data.data ?? [];
      const roles: Role[] = rolesRes.data.data ?? [];
      const assignments: TenantRoleEntry[] = assignmentsRes.data.data ?? [];

      // Build lookup maps
      const roleMap = new Map<string, string>();
      for (const role of roles) {
        roleMap.set(role.id, role.name);
      }

      const tenantAssignmentMap = new Map<string, string[]>();
      for (const entry of assignments) {
        const names = entry.roleIds
          .map((roleId) => roleMap.get(roleId) ?? `Unknown (${roleId.slice(0, 8)}…)`)
          .filter(Boolean);
        tenantAssignmentMap.set(entry.tenantId, names);
      }

      // Build rows: all tenants, mark configured vs not
      const tableRows: TenantAccessRow[] = tenants.map((tenant) => {
        const assignedRoles = tenantAssignmentMap.get(tenant.id);
        return {
          tenantId: tenant.id,
          tenantName: tenant.name,
          tenantCode: tenant.code,
          configured: assignedRoles !== undefined && assignedRoles.length > 0,
          roleNames: assignedRoles ?? [],
        };
      });

      // Sort: configured tenants first, then alphabetically
      tableRows.sort((a, b) => {
        if (a.configured !== b.configured) return a.configured ? -1 : 1;
        return a.tenantName.localeCompare(b.tenantName);
      });

      setRows(tableRows);
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Failed to load tenant access data';
      setError(message);
    }
    setLoading(false);
  }, [formId]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return <ErrorState message={error} onRetry={loadData} />;
  }

  if (rows.length === 0) {
    return (
      <Box sx={{ textAlign: 'center', py: 4 }}>
        <Info sx={{ color: 'text.secondary', fontSize: 40, mb: 1 }} />
        <Typography color="text.secondary">No tenants found.</Typography>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="subtitle2" sx={{ mb: 2 }}>
        Tenant Role Assignments
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
        This is a read-only view showing which tenants have configured role access for this global
        form. Tenant Admins manage their own role assignments.
      </Typography>

      <TableContainer>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell sx={{ fontWeight: 600 }}>Tenant</TableCell>
              <TableCell sx={{ fontWeight: 600 }}>Code</TableCell>
              <TableCell sx={{ fontWeight: 600 }}>Assigned Roles</TableCell>
              <TableCell sx={{ fontWeight: 600 }}>Status</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.map((row) => (
              <TableRow key={row.tenantId} hover>
                <TableCell>{row.tenantName}</TableCell>
                <TableCell>
                  <code>{row.tenantCode}</code>
                </TableCell>
                <TableCell>
                  {row.configured ? (
                    <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap' }}>
                      {row.roleNames.map((name) => (
                        <Chip key={name} label={name} size="small" variant="outlined" />
                      ))}
                    </Box>
                  ) : (
                    <Typography variant="body2" color="text.secondary" fontStyle="italic">
                      —
                    </Typography>
                  )}
                </TableCell>
                <TableCell>
                  <Chip
                    label={row.configured ? 'Configured' : 'Not configured'}
                    size="small"
                    color={row.configured ? 'success' : 'default'}
                    variant="outlined"
                  />
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}
