/**
 * FormSection — Renders a layout section with a grid of fields.
 *
 * Layout sections can be collapsible and can arrange fields in 1, 2, or 3 columns.
 */

import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { Card, CardContent, CardHeader, Collapse, Grid, IconButton } from '@mui/material';
import React, { useState } from 'react';

import type { FieldDefinition, LayoutSection } from '../hooks/useForm.types';

import { FormFieldRenderer } from './FormFieldRenderer';

export interface FormSectionProps {
  section: LayoutSection;
  fields: FieldDefinition[];
  fieldValues: Record<string, unknown>;
  mode: 'create' | 'edit' | 'view';
  onFieldChange?: (columnCode: string, value: unknown) => void;
  errors?: Record<string, string>;
  disabled?: boolean;
}

/**
 * Renders a single layout section with its fields arranged in columns.
 */
export const FormSection: React.FC<FormSectionProps> = ({
  section,
  fields,
  fieldValues,
  mode,
  onFieldChange,
  errors,
  disabled,
}) => {
  const [collapsed, setCollapsed] = useState(false);

  const columns = section.columns || 1;

  // Resolve fields in this section by their fieldId
  const sectionFields = section.fieldIds
    .map((fid) => fields.find((f) => f.fieldId === fid))
    .filter((f): f is FieldDefinition => !!f);

  if (sectionFields.length === 0) {
    return null;
  }

  const collapsible = section.collapsible === true;

  return (
    <Card variant="outlined" sx={{ mb: 2 }}>
      {section.label && (
        <CardHeader
          title={section.label}
          titleTypographyProps={{ variant: 'subtitle1', fontWeight: 600 }}
          action={
            collapsible ? (
              <IconButton
                size="small"
                onClick={() => setCollapsed((prev) => !prev)}
                aria-label={collapsed ? 'Expand section' : 'Collapse section'}
              >
                {collapsed ? <ExpandMoreIcon /> : <ExpandLessIcon />}
              </IconButton>
            ) : undefined
          }
          sx={{ pb: 0 }}
        />
      )}
      <Collapse in={!collapsed} timeout="auto">
        <CardContent sx={{ pt: 1 }}>
          <Grid container spacing={2}>
            {sectionFields.map((field) => (
              <Grid item xs={12} sm={columns >= 2 ? 6 : 12} md={12 / columns} key={field.fieldId}>
                <FormFieldRenderer
                  field={field}
                  value={fieldValues[field.columnCode]}
                  mode={mode}
                  onChange={onFieldChange}
                  error={errors?.[field.columnCode]}
                  disabled={disabled}
                />
              </Grid>
            ))}
          </Grid>
        </CardContent>
      </Collapse>
    </Card>
  );
};

export default FormSection;
