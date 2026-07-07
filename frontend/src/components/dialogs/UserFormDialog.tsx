import {
  Alert,
  Button,
  Checkbox,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  FormControlLabel,
  MenuItem,
  TextField,
} from '@mui/material';
import { useState, useEffect } from 'react';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface UserFormData {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phone: string;
  birthDate: string;
  emailVerified: string;
  status: string;
  password: string;
  website: string;
  employeeId: string;
  address: string;
}

interface User {
  id: string;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  displayName?: string;
  birthDate?: string;
  emailVerified?: boolean;
  website?: string;
  employeeId?: string;
  address?: string;
  status: string;
  roles: string[];
  createdAt: string;
}

interface Props {
  open: boolean;
  user: User | null;
  onClose: () => void;
  onSaved: () => void;
}

const emptyForm: UserFormData = {
  username: '',
  email: '',
  firstName: '',
  lastName: '',
  phone: '',
  birthDate: '',
  emailVerified: 'true',
  status: 'ACTIVE',
  password: '',
  website: '',
  employeeId: '',
  address: '',
};

export function UserFormDialog({ open, user, onClose, onSaved }: Props) {
  const isEdit = !!user;
  const [form, setForm] = useState<UserFormData>(emptyForm);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (open) {
      if (user) {
        setForm({
          username: user.username,
          email: user.email,
          firstName: user.firstName ?? '',
          lastName: user.lastName ?? '',
          phone: '',
          birthDate: user.birthDate ?? '',
          emailVerified: String(user.emailVerified ?? true),
          website: user.website ?? '',
          employeeId: user.employeeId ?? '',
          address: user.address ?? '',
          status: user.status,
          password: '',
        });
      } else {
        setForm(emptyForm);
      }
      setError(null);
    }
  }, [open, user]);

  const handleChange = (field: keyof UserFormData) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
  };

  const handleCheckbox =
    (field: keyof UserFormData) => (e: React.ChangeEvent<HTMLInputElement>) => {
      setForm((prev) => ({ ...prev, [field]: e.target.checked ? 'true' : 'false' }));
    };

  const handleSubmit = async () => {
    setSaving(true);
    setError(null);
    try {
      const body: Record<string, unknown> = {
        username: form.username,
        email: form.email,
        firstName: form.firstName || null,
        lastName: form.lastName || null,
        phone: form.phone || null,
        birthDate: form.birthDate || null,
        emailVerified: form.emailVerified === 'true',
        website: form.website || null,
        employeeId: form.employeeId || null,
        address: form.address || null,
        status: form.status,
      };
      if (!isEdit) {
        body.passwordHash = form.password;
      }
      if (isEdit) {
        await apiClient.put(ENDPOINTS.identity.user(user!.id), body);
      } else {
        await apiClient.post(ENDPOINTS.identity.users, body);
      }
      onSaved();
      onClose();
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Failed to save user';
      setError(msg);
    } finally {
      setSaving(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{isEdit ? 'Edit User' : 'Create User'}</DialogTitle>
      <DialogContent>
        {error && (
          <Alert severity="error" sx={{ mb: 2, mt: 1 }}>
            {error}
          </Alert>
        )}
        <TextField
          label="Username"
          value={form.username}
          onChange={handleChange('username')}
          fullWidth
          required
          margin="normal"
        />
        <TextField
          label="Email"
          type="email"
          value={form.email}
          onChange={handleChange('email')}
          fullWidth
          required
          margin="normal"
        />
        <TextField
          label="First Name"
          value={form.firstName}
          onChange={handleChange('firstName')}
          fullWidth
          margin="normal"
        />
        <TextField
          label="Last Name"
          value={form.lastName}
          onChange={handleChange('lastName')}
          fullWidth
          margin="normal"
        />
        <TextField
          label="Phone"
          value={form.phone}
          onChange={handleChange('phone')}
          fullWidth
          margin="normal"
        />
        <TextField
          label="Birth Date"
          type="date"
          value={form.birthDate}
          onChange={handleChange('birthDate')}
          fullWidth
          margin="normal"
          InputLabelProps={{ shrink: true }}
        />
        <FormControl fullWidth margin="normal">
          <FormControlLabel
            control={
              <Checkbox
                checked={form.emailVerified === 'true'}
                onChange={handleCheckbox('emailVerified')}
              />
            }
            label="Email Verified"
          />
        </FormControl>
        <TextField
          label="Website"
          type="url"
          value={form.website}
          onChange={handleChange('website')}
          fullWidth
          margin="normal"
          placeholder="https://example.com"
        />
        <TextField
          label="Employee ID"
          type="number"
          value={form.employeeId}
          onChange={handleChange('employeeId')}
          fullWidth
          margin="normal"
        />
        <TextField
          label="Address"
          value={form.address}
          onChange={handleChange('address')}
          fullWidth
          margin="normal"
          multiline
          rows={2}
          placeholder="Street, City, Country"
        />
        <TextField
          label="Status"
          value={form.status}
          onChange={handleChange('status')}
          fullWidth
          select
          margin="normal"
        >
          <MenuItem value="ACTIVE">Active</MenuItem>
          <MenuItem value="INACTIVE">Inactive</MenuItem>
          <MenuItem value="SUSPENDED">Suspended</MenuItem>
        </TextField>
        {!isEdit && (
          <TextField
            label="Password"
            type="password"
            value={form.password}
            onChange={handleChange('password')}
            fullWidth
            required
            margin="normal"
          />
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button variant="contained" onClick={handleSubmit} disabled={saving}>
          {saving ? 'Saving...' : isEdit ? 'Update' : 'Create'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
