/**
 * Maps a FormDefinition from the runtime API to a RuntimeMetadataBundle
 * consumable by the RuntimeRenderer.
 *
 * The API returns a flat form definition (fields + layout sections),
 * while the RuntimeRenderer expects a structured metadata bundle
 * (model + views + actions + permissions + workflow).
 */
import type { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import type { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import type { FieldDefinition as SchemaField } from '@/core/metadata/schema/field/FieldDefinition';
import { ViewType } from '@/core/metadata/schema/view/ViewDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import type { FormDefinition, FieldDefinition as ApiField, LayoutSection } from '../hooks/useForm.types';

/**
 * Converts an API field type string to the schema field type.
 */
function mapFieldType(apiType: string): SchemaField['type'] {
  const typeMap: Record<string, SchemaField['type']> = {
    string: 'TEXT',
    text: 'TEXTAREA',
    number: 'NUMBER',
    integer: 'NUMBER',
    decimal: 'DECIMAL',
    boolean: 'BOOLEAN',
    date: 'DATE',
    datetime: 'DATETIME',
    enum: 'SELECT',
    many2one: 'MANY_TO_ONE',
  };
  return typeMap[apiType] ?? 'TEXT';
}

/**
 * Maps a form definition to the metadata bundle format.
 */
export function formDefinitionToBundle(formDef: FormDefinition): RuntimeMetadataBundle {
  const modelCode = formDef.modelName;
  const modelLabel = formDef.modelLabel ?? formDef.formLabel;

  // Build model fields from form fields
  const modelFields: SchemaField[] = formDef.fields.map((f: ApiField) => ({
    id: f.fieldId,
    code: f.columnCode,
    name: f.label,
    description: '',
    version: 1,
    active: true,
    type: mapFieldType(f.type),
    required: f.required,
    readonly: f.readOnly,
    hidden: !f.visible,
    defaultValue: f.defaultValue,
    order: f.position,
  }));

  // Build model
  const model: ModelDefinition = {
    id: `model_${modelCode}`,
    code: modelCode,
    name: modelLabel,
    description: formDef.formLabel,
    version: 1,
    active: true,
    tableName: formDef.tableName,
    auditable: true,
    workflowEnabled: false,
    tenantAware: true,
    fields: modelFields,
  };

  // Build layout sections
  const childSections = (formDef.sections ?? []).map((s: LayoutSection) => ({
    type: LayoutType.SECTION as const,
    config: {
      id: s.sectionId,
      code: s.code,
      label: s.label,
      columns: s.columns,
      collapsible: s.collapsible,
      position: s.position,
    },
    children: s.fieldIds?.map((fid) => ({
      type: LayoutType.GROUP as const,
      config: { fieldId: fid },
    })),
  }));

  // Build view
  const formView = {
    id: `view_${formDef.formCode}_form`,
    code: `${formDef.formCode}_form`,
    name: `${formDef.formLabel} Form`,
    modelCode,
    viewType: ViewType.FORM,
    title: formDef.formLabel,
    description: '',
    version: 1,
    active: true,
    layout: {
      type: LayoutType.PAGE as const,
      children: childSections.length > 0 ? childSections : [{
        type: LayoutType.SECTION as const,
        config: { code: 'default', label: 'General', columns: 2 },
        children: formDef.fields.map((f) => ({
          type: LayoutType.GROUP as const,
          config: { fieldId: f.fieldId },
        })),
      }],
    },
  };

  return {
    model,
    views: [formView],
    actions: [],
    permissions: [],
    workflow: undefined,
  };
}
