/**
 * FormFieldRenderer — Resolves a field's type to the appropriate MUI component.
 *
 * Uses the fieldRegistry when available, falling back to built-in renderers
 * for standard types: string, text, integer, decimal, boolean, date, datetime,
 * many2one, enum.
 */

import React, { useCallback } from 'react';
import {
  Checkbox,
  FormControl,
  FormControlLabel,
  FormHelperText,
  InputLabel,
  Select,
  TextField,
} from '@mui/material';

import type { FieldDefinition } from '../hooks/useForm.types';

export interface FormFieldRendererProps {
  field: FieldDefinition;
  value: unknown;
  mode: 'create' | 'edit' | 'view';
  onChange?: (columnCode: string, value: unknown) => void;
  error?: string;
  disabled?: boolean;
}

/**
 * Maps backend column types to HTML input types.
 */
function mapInputType(fieldType: string): string {
  switch (fieldType) {
    case 'integer':
    case 'decimal':
      return 'number';
    case 'date':
      return 'date';
    case 'datetime':
      return 'datetime-local';
    case 'boolean':
      return 'checkbox';
    default:
      return 'text';
  }
}

/**
 * Renders a single form field based on its type definition.
 */
export const FormFieldRenderer: React.FC<FormFieldRendererProps> = ({
  field,
  value,
  mode,
  onChange,
  error,
  disabled,
}) => {
  const isView = mode === 'view';
  const isDisabled = disabled || isView || field.readOnly;

  const handleChange = useCallback(
    (newValue: unknown) => {
      if (onChange && !isView) {
        onChange(field.columnCode, newValue);
      }
    },
    [field.columnCode, isView, onChange]
  );

  const inputType = mapInputType(field.type);
  const label = field.label;
  const placeholder = field.placeholder;
  const isRequired = field.required && !isView;

  // ---- Read-only / view mode ----
  if (isView) {
    return (
      <TextField
        label={label}
        value={value ?? ''}
        fullWidth
        disabled
        variant="filled"
        size="small"
      />
    );
  }

  // ---- Boolean ----
  if (field.type === 'boolean') {
    return (
      <FormControl error={!!error} fullWidth>
        <FormControlLabel
          control={
            <Checkbox
              checked={!!value}
              onChange={(e) => handleChange(e.target.checked)}
              disabled={isDisabled}
            />
          }
          label={label}
        />
        {error && <FormHelperText>{error}</FormHelperText>}
      </FormControl>
    );
  }

  // ---- Enum ----
  if (field.type === 'enum' && field.enumOptions?.length) {
    return (
      <FormControl error={!!error} fullWidth size="small">
        <InputLabel>{label}</InputLabel>
        <Select
          value={value ?? ''}
          label={label}
          onChange={(e) => handleChange(e.target.value)}
          disabled={isDisabled}
          native
        >
          <option value="">{placeholder ?? `Select ${label}`}</option>
          {field.enumOptions.map((opt: string) => (
            <option key={opt} value={opt}>
              {opt}
            </option>
          ))}
        </Select>
        {error && <FormHelperText>{error}</FormHelperText>}
      </FormControl>
    );
  }

  // ---- many2one ----
  if (field.type === 'many2one') {
    // Fallback: render as text input with relation table hint
    return (
      <TextField
        label={label}
        value={value ?? ''}
        placeholder={placeholder ?? `Search ${field.relationTable ?? 'related record'}...`}
        onChange={(e) => handleChange(e.target.value)}
        error={!!error}
        helperText={error ?? (field.relationTable ? `Lookup: ${field.relationTable}` : undefined)}
        disabled={isDisabled}
        fullWidth
        size="small"
      />
    );
  }

  // ---- Text / Textarea ----
  if (field.type === 'text') {
    return (
      <TextField
        label={label}
        value={value ?? ''}
        placeholder={placeholder}
        onChange={(e) => handleChange(e.target.value)}
        error={!!error}
        helperText={error}
        disabled={isDisabled}
        required={isRequired}
        fullWidth
        multiline
        minRows={3}
        size="small"
      />
    );
  }

  // ---- String, integer, decimal, date, datetime (default) ----
  return (
    <TextField
      label={label}
      value={value ?? ''}
      type={inputType}
      placeholder={placeholder}
      onChange={(e) => {
        const raw = e.target.value;
        if (inputType === 'number') {
          handleChange(raw === '' ? undefined : Number(raw));
        } else {
          handleChange(raw);
        }
      }}
      error={!!error}
      helperText={error}
      disabled={isDisabled}
      required={isRequired}
      fullWidth
      size="small"
      InputLabelProps={
        inputType === 'date' || inputType === 'datetime-local' ? { shrink: true } : undefined
      }
    />
  );
};

export default FormFieldRenderer;
