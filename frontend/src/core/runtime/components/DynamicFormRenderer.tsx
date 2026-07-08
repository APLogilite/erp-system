/**
 * DynamicFormRenderer — The core dynamic form renderer component.
 *
 * Takes a FormDefinition and renders fields organized in layout sections,
 * using the appropriate MUI component for each field type.
 *
 * Supports three modes: create, edit, view.
 */

import { Alert, Box, CircularProgress, Stack, Typography } from '@mui/material';
import React, { useCallback, useMemo } from 'react';

import type { FieldDefinition, FormDefinition, LayoutSection } from '../hooks/useForm.types';

import { FormSection } from './FormSection';

export interface DynamicFormRendererProps {
  /** The form definition from useForm() or fetchFormDefinition() */
  formDefinition: FormDefinition | undefined;
  /** Current record data (keyed by columnCode) */
  record?: Record<string, unknown>;
  /** Form mode */
  mode: 'create' | 'edit' | 'view';
  /** Called when any field value changes */
  onChange?: (columnCode: string, value: unknown) => void;
  /** Field-level validation errors (keyed by columnCode) */
  errors?: Record<string, string>;
  /** Global disable (e.g., during save) */
  disabled?: boolean;
  /** Whether definition is loading */
  isLoading?: boolean;
}

/**
 * Renders the complete dynamic form based on a FormDefinition.
 */
export const DynamicFormRenderer: React.FC<DynamicFormRendererProps> = ({
  formDefinition,
  record,
  mode,
  onChange,
  errors,
  disabled,
  isLoading,
}) => {
  // Handle field value changes
  const handleFieldChange = useCallback(
    (columnCode: string, value: unknown) => {
      if (onChange) {
        onChange(columnCode, value);
      }
    },
    [onChange]
  );

  // Memoize section + field lookup
  const sections = useMemo<(LayoutSection & { fields: FieldDefinition[] })[]>(() => {
    if (!formDefinition) return [];

    const fieldMap = new Map<string, FieldDefinition>();
    for (const f of formDefinition.fields) {
      fieldMap.set(f.fieldId, f);
    }

    // If no sections defined, create one default section with all fields
    const rawSections = formDefinition.sections?.length
      ? formDefinition.sections
      : [
          {
            sectionId: '__default__',
            code: 'default',
            label: formDefinition.formLabel,
            collapsible: false,
            columns: 1,
            position: 0,
            fieldIds: formDefinition.fields.map((f) => f.fieldId),
          },
        ];

    return rawSections.map((sec) => ({
      ...sec,
      fields: sec.fieldIds.map((fid) => fieldMap.get(fid)).filter((f): f is FieldDefinition => !!f),
    }));
  }, [formDefinition]);

  // Loading state
  if (isLoading || !formDefinition) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 6 }}>
        <CircularProgress />
      </Box>
    );
  }

  // No sections
  if (sections.length === 0) {
    return (
      <Alert severity="info" sx={{ mt: 2 }}>
        No fields configured for form &ldquo;{formDefinition.formLabel}&rdquo;.
      </Alert>
    );
  }

  // Current field values from record or empty object
  const fieldValues = record ?? {};

  return (
    <Stack spacing={1}>
      {/* Form title */}
      <Typography variant="h5" component="h1" gutterBottom>
        {formDefinition.formLabel}
      </Typography>

      {formDefinition.fields && formDefinition.fields.length === 0 && (
        <Alert severity="warning" sx={{ mb: 2 }}>
          This form has no fields configured. Use the Form Designer to add fields.
        </Alert>
      )}

      {/* Render sections */}
      {sections.map((section) => (
        <FormSection
          key={section.sectionId}
          section={section}
          fields={section.fields}
          fieldValues={fieldValues}
          mode={mode}
          onFieldChange={handleFieldChange}
          errors={errors}
          disabled={disabled}
        />
      ))}
    </Stack>
  );
};

export default DynamicFormRenderer;
