import {
  ArrowBack,
  Business,
  CorporateFare,
  Store,
  AccountTree,
  Badge,
  ArrowForward,
} from '@mui/icons-material';
import {
  Box,
  Button,
  Card,
  CardContent,
  Typography,
  MenuItem,
  TextField,
  useTheme,
  Avatar,
  Alert,
  CircularProgress,
} from '@mui/material';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';
import type { ApiError } from '@/core/api/errors';
import { useAuthStore } from '@/core/auth/authStore';

interface ContextOption {
  id: string;
  code: string;
  name: string;
  parentId?: string;
}

interface RoleScope {
  fullAccess: boolean;
  tenantId: string | null;
  organizationIds: string[];
  companyIds: string[];
  branchIds: string[];
}

interface ContextOptionsResponse {
  tenants: ContextOption[];
  organizations: ContextOption[];
  companies: ContextOption[];
  branches: ContextOption[];
  departments: ContextOption[];
  roles: string[];
  roleScopes: Record<string, RoleScope>;
}

interface ContextProfile {
  organizationId: string;
  companyId: string;
  branchId: string;
  roleCode: string;
}

export function ContextSelectPage() {
  const theme = useTheme();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const user = useAuthStore((s) => s.user);

  const [selectedRole, setSelectedRole] = useState('');
  const [selectedOrg, setSelectedOrg] = useState('');
  const [selectedCompany, setSelectedCompany] = useState('');
  const [selectedBranch, setSelectedBranch] = useState('');
  const [switchError, setSwitchError] = useState('');

  const { data: current } = useQuery({
    queryKey: ['context', 'current'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.context.current);
      return res.data.data || res.data;
    },
  });

  const hasCurrentContext = !!(current?.roles?.length || current?.organizationId);

  const {
    data: options,
    isLoading,
    isError,
  } = useQuery<ContextOptionsResponse>({
    queryKey: ['context', 'options-for-select'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.context.options);
      return res.data.data || res.data;
    },
    retry: 1,
  });

  // Selected role scope
  const roleScope = useMemo<RoleScope | null>(() => {
    if (!selectedRole || !options?.roleScopes) return null;
    return options.roleScopes[selectedRole] ?? null;
  }, [selectedRole, options?.roleScopes]);

  // Tenant from role scope
  const selectedTenant = roleScope?.tenantId ?? '';

  // Accessible org IDs (filtered by role scope)
  const accessibleOrgIds = useMemo(() => {
    if (!roleScope) return [];
    if (roleScope.fullAccess) return (options?.organizations ?? []).map((o) => o.id);
    return roleScope.organizationIds.filter((id) =>
      options?.organizations?.some((o) => o.id === id)
    );
  }, [roleScope, options?.organizations]);

  const filteredOrgs = useMemo(() => {
    return (options?.organizations ?? []).filter(
      (o) => accessibleOrgIds.includes(o.id) && o.parentId === selectedTenant
    );
  }, [options?.organizations, accessibleOrgIds, selectedTenant]);

  const accessibleCoIds = useMemo(() => {
    if (!roleScope) return [];
    if (roleScope.fullAccess) return (options?.companies ?? []).map((c) => c.id);
    return roleScope.companyIds.filter((id) => options?.companies?.some((c) => c.id === id));
  }, [roleScope, options?.companies]);

  const filteredCompanies = useMemo(() => {
    return (options?.companies ?? []).filter(
      (c) => accessibleCoIds.includes(c.id) && c.parentId === selectedOrg
    );
  }, [options?.companies, accessibleCoIds, selectedOrg]);

  const accessibleBrIds = useMemo(() => {
    if (!roleScope) return [];
    if (roleScope.fullAccess) return (options?.branches ?? []).map((b) => b.id);
    return roleScope.branchIds.filter((id) => options?.branches?.some((b) => b.id === id));
  }, [roleScope, options?.branches]);

  const filteredBranches = useMemo(() => {
    return (options?.branches ?? []).filter(
      (b) => accessibleBrIds.includes(b.id) && b.parentId === selectedCompany
    );
  }, [options?.branches, accessibleBrIds, selectedCompany]);

  // Compute profiles for auto-route
  const profiles = useMemo(() => {
    if (!options) return [];
    const result: ContextProfile[] = [];
    for (const role of options.roles ?? []) {
      const scope = options.roleScopes?.[role];
      if (!scope) continue;
      const orgIds = scope.fullAccess
        ? options.organizations.map((o) => o.id)
        : scope.organizationIds;
      for (const oid of orgIds) {
        const coIds = scope.fullAccess
          ? options.companies.filter((c) => c.parentId === oid).map((c) => c.id)
          : scope.companyIds;
        for (const cid of coIds) {
          const brIds = scope.fullAccess
            ? options.branches.filter((b) => b.parentId === cid).map((b) => b.id)
            : scope.branchIds;
          for (const bid of brIds) {
            result.push({ organizationId: oid, companyId: cid, branchId: bid, roleCode: role });
          }
        }
      }
    }
    return result;
  }, [options]);

  // Auto-route: single profile → skip selection page
  const switchMutation = useMutation({
    mutationFn: async (profile: ContextProfile) => {
      await apiClient.post(ENDPOINTS.context.switch, {
        organizationId: profile.organizationId,
        companyId: profile.companyId,
        branchId: profile.branchId,
        roleCode: profile.roleCode,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['context'] });
      navigate('/app/dashboard', { replace: true });
    },
    onError: (err: ApiError) => {
      setSwitchError(err?.message || 'Auto-route failed');
    },
  });

  useEffect(() => {
    if (profiles.length === 1 && !switchMutation.isPending && selectedTenant) {
      setSwitchError('');
      const p = profiles[0];
      if (!selectedRole) setSelectedRole(p.roleCode);
      if (!selectedOrg) setSelectedOrg(p.organizationId);
      if (!selectedCompany) setSelectedCompany(p.companyId);
      if (!selectedBranch) setSelectedBranch(p.branchId);
      switchMutation.mutate(p);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [profiles.length, selectedTenant]);

  // Cascading switch
  const cascadingSwitch = useMutation({
    mutationFn: async () => {
      setSwitchError('');
      const body: Record<string, string | undefined> = {
        organizationId: selectedOrg || undefined,
        companyId: selectedCompany || undefined,
        branchId: selectedBranch || undefined,
        roleCode: selectedRole || undefined,
      };
      await apiClient.post(ENDPOINTS.context.switch, body);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['context'] });
      navigate('/app/dashboard', { replace: true });
    },
    onError: (err: ApiError) => {
      setSwitchError(err?.message || 'Unknown error');
    },
  });

  // Pre-populate from existing context (e.g. when coming from "Change Workspace")
  useEffect(() => {
    if (!current || !options) return;
    const role = current.roles?.[0];
    if (role && options.roles?.includes(role) && !selectedRole) setSelectedRole(role);
  }, [current, options, selectedRole]);

  // Auto-select single-option levels as they become available
  useEffect(() => {
    if (options?.roles?.length === 1 && !selectedRole) setSelectedRole(options.roles[0]);
  }, [options?.roles, selectedRole]);

  useEffect(() => {
    if (filteredOrgs.length === 1 && !selectedOrg) setSelectedOrg(filteredOrgs[0].id);
  }, [filteredOrgs, selectedOrg]);

  useEffect(() => {
    if (filteredCompanies.length === 1 && !selectedCompany)
      setSelectedCompany(filteredCompanies[0].id);
  }, [filteredCompanies, selectedCompany]);

  useEffect(() => {
    if (filteredBranches.length === 1 && !selectedBranch) setSelectedBranch(filteredBranches[0].id);
  }, [filteredBranches, selectedBranch]);

  const isLoadingAuto = profiles.length === 1 && switchMutation.isPending;

  if (isLoading || isLoadingAuto) {
    return (
      <Box
        sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh' }}
      >
        <CircularProgress />
      </Box>
    );
  }

  if (isError) {
    return (
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '100vh',
          p: 2,
        }}
      >
        <Card sx={{ maxWidth: 480, p: 3, borderRadius: 4 }}>
          <Alert severity="error" sx={{ mb: 2 }}>
            Failed to load workspace options.
          </Alert>
          <Button variant="contained" onClick={() => navigate('/app/dashboard', { replace: true })}>
            Go to Dashboard
          </Button>
        </Card>
      </Box>
    );
  }

  const roleSingle = (options?.roles?.length ?? 0) === 1;
  const orgSingle = filteredOrgs.length === 1;
  const coSingle = filteredCompanies.length === 1;
  const brSingle = filteredBranches.length === 1;

  return (
    <Box
      sx={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100vh',
        background:
          theme.palette.mode === 'dark'
            ? 'radial-gradient(circle at 50% 50%, #151a30 0%, #0b0e1a 100%)'
            : 'radial-gradient(circle at 50% 50%, #f4f6fb 0%, #e2e8f0 100%)',
        p: 2,
      }}
    >
      <Card sx={{ width: '100%', maxWidth: 520, borderRadius: 4, overflow: 'hidden' }}>
        <Box sx={{ height: 6, background: 'linear-gradient(90deg, #1976d2 0%, #82b1ff 100%)' }} />
        <CardContent sx={{ p: 4 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 2, ml: -1 }}>
            <Button
              startIcon={<ArrowBack />}
              onClick={() =>
                navigate(hasCurrentContext ? '/app/dashboard' : '/login', { replace: true })
              }
              sx={{ textTransform: 'none', color: 'text.secondary' }}
            >
              Back
            </Button>
          </Box>
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mb: 3 }}>
            <Avatar sx={{ width: 56, height: 56, bgcolor: 'primary.main', mb: 2, fontSize: 24 }}>
              {user?.displayName?.charAt(0)?.toUpperCase() ?? 'U'}
            </Avatar>
            <Typography variant="h5" component="h1" fontWeight={700} gutterBottom>
              Select Your Workspace
            </Typography>
            <Typography variant="body2" color="text.secondary" textAlign="center">
              Welcome, <strong>{user?.displayName ?? user?.username}</strong>. Pick a role and
              workspace to begin.
            </Typography>
          </Box>

          {(cascadingSwitch.isError || switchMutation.isError || switchError) && (
            <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
              {switchError || 'Failed to set workspace. Please try again.'}
            </Alert>
          )}

          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2.5, mb: 3 }}>
            {/* Role selector — at top */}
            {options?.roles && options.roles.length > 0 && (
              <SelectField
                label="Role"
                icon={<Badge fontSize="small" color="action" />}
                value={selectedRole}
                onChange={(v) => {
                  setSelectedRole(v);
                  setSelectedOrg('');
                  setSelectedCompany('');
                  setSelectedBranch('');
                }}
                options={options.roles.map((r) => ({ id: r, name: r }))}
                disabled={roleSingle && !!selectedRole}
              />
            )}

            {/* Tenant — auto-filled from role scope, no dropdown needed */}
            {selectedTenant && (
              <SelectField
                label="Tenant"
                icon={<Business fontSize="small" color="action" />}
                value={selectedTenant}
                onChange={() => {}}
                options={[
                  {
                    id: selectedTenant,
                    name:
                      options?.tenants?.find((t) => t.id === selectedTenant)?.name ??
                      selectedTenant,
                  },
                ]}
                disabled
              />
            )}

            {selectedRole && filteredOrgs.length > 0 && (
              <SelectField
                label="Organization"
                icon={<CorporateFare fontSize="small" color="action" />}
                value={selectedOrg}
                onChange={(v) => {
                  setSelectedOrg(v);
                  setSelectedCompany('');
                  setSelectedBranch('');
                }}
                options={filteredOrgs}
                disabled={orgSingle && !!selectedOrg}
              />
            )}

            {selectedOrg && filteredCompanies.length > 0 && (
              <SelectField
                label="Company"
                icon={<Store fontSize="small" color="action" />}
                value={selectedCompany}
                onChange={(v) => {
                  setSelectedCompany(v);
                  setSelectedBranch('');
                }}
                options={filteredCompanies}
                disabled={coSingle && !!selectedCompany}
              />
            )}

            {selectedCompany && filteredBranches.length > 0 && (
              <SelectField
                label="Branch"
                icon={<AccountTree fontSize="small" color="action" />}
                value={selectedBranch}
                onChange={setSelectedBranch}
                options={filteredBranches}
                disabled={brSingle && !!selectedBranch}
              />
            )}
          </Box>

          <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Button
              variant="contained"
              size="large"
              onClick={() => cascadingSwitch.mutate()}
              disabled={cascadingSwitch.isPending || !selectedRole}
              endIcon={
                cascadingSwitch.isPending ? (
                  <CircularProgress size={18} color="inherit" />
                ) : (
                  <ArrowForward />
                )
              }
              sx={{ py: 1.2, px: 4, borderRadius: 2, fontWeight: 600, textTransform: 'none' }}
            >
              {cascadingSwitch.isPending ? 'Setting workspace...' : 'Enter Workspace'}
            </Button>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
}

function SelectField({
  label,
  icon,
  value,
  onChange,
  options,
  disabled,
}: {
  label: string;
  icon: React.ReactNode;
  value: string;
  onChange: (v: string) => void;
  options: { id: string; name: string }[];
  disabled?: boolean;
}) {
  return (
    <Box>
      <Typography
        variant="subtitle2"
        sx={{ mb: 0.5, display: 'flex', alignItems: 'center', gap: 0.5 }}
      >
        {icon} {label}
      </Typography>
      <TextField
        select
        fullWidth
        value={value}
        onChange={(e) => onChange(e.target.value)}
        disabled={disabled}
      >
        {options.map((opt) => (
          <MenuItem key={opt.id} value={opt.id}>
            {opt.name}
          </MenuItem>
        ))}
      </TextField>
    </Box>
  );
}
