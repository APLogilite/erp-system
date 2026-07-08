export interface FormDefinition {
  id: string;
  code: string;
  label: string;
  modelName: string;
  scope: 'global' | 'tenant';
  description?: string;
  isActive: boolean;
}

export interface FormField {
  id: string;
  formId: string;
  columnCode: string;
  labelOverride?: string;
  placeholder?: string;
  defaultValue?: string;
  isVisible: boolean;
  isEditable: boolean;
  isRequired: boolean;
  position: number;
}

export interface LayoutSection {
  id: string;
  formId: string;
  label: string;
  columns: number;
  collapsible: boolean;
  position: number;
  fields?: LayoutSectionField[];
}

export interface LayoutSectionField {
  id: string;
  sectionId: string;
  fieldId: string;
  position: number;
}

export interface AvailableTable {
  tableCode: string;
  tableLabel: string;
  columnCount: number;
}

export type FormScope = 'global' | 'tenant';
