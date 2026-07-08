import { Add, Delete } from '@mui/icons-material';
import {
  Box,
  Button,
  Card,
  CardContent,
  FormControlLabel,
  Grid,
  IconButton,
  MenuItem,
  Switch,
  TextField,
  Typography,
} from '@mui/material';
import { useState } from 'react';

import { useAddSection, useDeleteSection, useFormLayout, useUpdateSection } from '../hooks/useFormLayout';
import type { LayoutSection } from '../types';

interface Props {
  formId: string;
}

export function LayoutTab({ formId }: Props) {
  const { data: sections, isLoading } = useFormLayout(formId);
  const addSection = useAddSection(formId);
  const updateSection = useUpdateSection(formId);
  const deleteSection = useDeleteSection(formId);

  const [newLabel, setNewLabel] = useState('');

  const handleAdd = () => {
    if (!newLabel.trim()) return;
    addSection.mutate({ label: newLabel, columns: 1, collapsible: false, position: (sections?.length ?? 0) + 1 });
    setNewLabel('');
  };

  const handleUpdate = (section: LayoutSection, key: string, value: unknown) => {
    updateSection.mutate({ sectionId: section.id, data: { [key]: value } });
  };

  if (isLoading) return <Box sx={{ p: 2 }}>Loading layout...</Box>;

  return (
    <Box>
      <Typography variant="subtitle2" sx={{ mb: 2 }}>
        Form Layout Sections
      </Typography>

      {(!sections || sections.length === 0) && (
        <Typography color="text.secondary" sx={{ mb: 2 }}>
          No sections defined. Add sections to organize form fields.
        </Typography>
      )}

      {sections?.map((section) => (
        <Card key={section.id} variant="outlined" sx={{ mb: 2 }}>
          <CardContent sx={{ '&:last-child': { pb: 2 } }}>
            <Grid container spacing={2} alignItems="center">
              <Grid item xs={12} sm={4}>
                <TextField size="small" fullWidth label="Section Label"
                  value={section.label}
                  onChange={(e) => handleUpdate(section, 'label', e.target.value)} />
              </Grid>
              <Grid item xs={6} sm={2}>
                <TextField size="small" fullWidth select label="Columns"
                  value={section.columns}
                  onChange={(e) => handleUpdate(section, 'columns', parseInt(e.target.value, 10))}>
                  <MenuItem value={1}>1</MenuItem>
                  <MenuItem value={2}>2</MenuItem>
                  <MenuItem value={3}>3</MenuItem>
                </TextField>
              </Grid>
              <Grid item xs={6} sm={3}>
                <FormControlLabel
                  control={<Switch size="small" checked={section.collapsible === true}
                    onChange={(e) => handleUpdate(section, 'collapsible', e.target.checked)} />}
                  label="Collapsible" />
              </Grid>
              <Grid item xs={12} sm={2}>
                <Typography variant="caption" color="text.secondary">
                  {(section.fields?.length ?? 0)} fields
                </Typography>
              </Grid>
              <Grid item xs={12} sm={1}>
                <IconButton color="error" size="small" onClick={() => deleteSection.mutate(section.id)}>
                  <Delete fontSize="small" />
                </IconButton>
              </Grid>
            </Grid>
          </CardContent>
        </Card>
      ))}

      <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
        <TextField size="small" placeholder="New section label" value={newLabel}
          onChange={(e) => setNewLabel(e.target.value)} sx={{ width: 240 }} />
        <Button startIcon={<Add />} variant="outlined" size="small"
          onClick={handleAdd} disabled={!newLabel.trim()}>
          Add Section
        </Button>
      </Box>
    </Box>
  );
}
