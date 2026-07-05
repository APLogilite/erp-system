import { useState, useEffect, type ComponentType } from 'react';

import { FieldDefinition } from '@/core/metadata/schema/field/FieldDefinition';
import { useRegistry } from '@/core/registry';
import { useExpression } from '@/runtime/expression/useExpression';
import { useFormState } from '@/runtime/state/formState';

interface FieldRendererProps {
  field: FieldDefinition;
}

export function FieldRenderer({ field }: FieldRendererProps) {
  const registry = useRegistry();
  const formState = useFormState();
  const isVisible = useExpression(field.visibleWhen, formState.values);
  const isReadOnly = useExpression(field.readonlyWhen, formState.values) || field.readonly;
  const isRequired = useExpression(field.requiredWhen, formState.values) || field.required;
  const value = formState.values[field.code] ?? field.defaultValue;

  const [Component, setComponent] = useState<ComponentType<Record<string, unknown>> | null>(null);

  useEffect(() => {
    let active = true;
    registry.fields.resolveField(field.type).then((component) => {
      if (active) {
        setComponent(() => component);
      }
    });
    return () => {
      active = false;
    };
  }, [field.type, registry.fields]);

  if (!isVisible) {
    return null;
  }

  if (!Component) {
    return <div>Resolving field...</div>;
  }

  return (
    <Component
      field={field}
      value={value}
      onChange={(nextValue: unknown) => formState.setValue(field.code, nextValue)}
      readOnly={isReadOnly}
      required={isRequired}
      error={formState.errors[field.code]}
    />
  );
}
