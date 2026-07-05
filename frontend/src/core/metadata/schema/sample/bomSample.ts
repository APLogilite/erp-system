import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const bomModel: ModelDefinition = {
  id: 'model_bom_001',
  code: 'bill_of_material',
  name: 'Bill of Material',
  description: 'Product manufacturing definition',
  version: 1,
  active: true,
  tableName: 'bill_of_materials',
  auditable: true,
  workflowEnabled: true,
  tenantAware: false,
  fields: [
    {
      id: 'field_bom_code',
      code: 'code',
      name: 'Code',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_bom_name',
      code: 'name',
      name: 'Name',
      type: 'TEXT',
      required: true,
      searchable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_bom_product',
      code: 'productId',
      name: 'Product',
      type: 'MANY_TO_ONE',
      required: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_bom_revision',
      code: 'revision',
      name: 'Revision',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_bom_status',
      code: 'status',
      name: 'Status',
      type: 'SELECT',
      required: false,
      readonly: true,
      filterable: true,
      version: 1,
      active: true,
      defaultValue: 'DRAFT',
      properties: {
        options: [
          { label: 'Draft', value: 'DRAFT' },
          { label: 'Active', value: 'ACTIVE' },
          { label: 'Archived', value: 'ARCHIVED' },
        ],
      },
    },
    {
      id: 'field_bom_eff_from',
      code: 'effectiveFrom',
      name: 'Effective From',
      type: 'DATE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_bom_eff_to',
      code: 'effectiveTo',
      name: 'Effective To',
      type: 'DATE',
      required: false,
      version: 1,
      active: true,
    },
  ],
};

export const bomFormView: ViewDefinition = {
  id: 'view_bom_form',
  code: 'bom_form',
  modelCode: 'bill_of_material',
  viewType: ViewType.FORM,
  name: 'BOM Form',
  title: 'Bill of Material Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'BOM Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'code' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'name' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'productId' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'status' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'revision' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'effectiveFrom' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'effectiveTo' } },
            ],
          },
        ],
      },
    ],
  },
};

export const bomGridView: ViewDefinition = {
  id: 'view_bom_grid',
  code: 'bom_list',
  modelCode: 'bill_of_material',
  viewType: ViewType.GRID,
  name: 'BOM Grid',
  title: 'Bill of Materials',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: ['code', 'name', 'productId', 'revision', 'status', 'effectiveFrom', 'effectiveTo'],
    },
  },
};

export const bomActions: ActionDefinition[] = [
  {
    id: 'act_bom_create',
    code: 'create_bom',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
  {
    id: 'act_bom_approve',
    code: 'approve',
    name: 'Approve',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'check',
  },
];

export const bomPermissions: PermissionDefinition[] = [
  {
    id: 'perm_bom_read',
    code: 'read_bom',
    name: 'Read BOM',
    version: 1,
    active: true,
    resource: 'bill_of_material',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_bom_write',
    code: 'write_bom',
    name: 'Write BOM',
    version: 1,
    active: true,
    resource: 'bill_of_material',
    permissionType: PermissionType.MODULE,
  },
];

export const bomBundle: RuntimeMetadataBundle = {
  model: bomModel,
  views: [bomFormView, bomGridView],
  actions: bomActions,
  permissions: bomPermissions,
};
