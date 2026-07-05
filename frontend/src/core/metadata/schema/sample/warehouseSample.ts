import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';
import { WorkflowDefinition } from '@/core/metadata/schema/workflow/WorkflowDefinition';

export const warehouseModel: ModelDefinition = {
  id: 'model_wh_001',
  code: 'warehouse',
  name: 'Warehouse',
  description: 'Storage facility configuration',
  version: 1,
  active: true,
  tableName: 'm1_warehouses',
  auditable: true,
  workflowEnabled: true,
  tenantAware: false,
  fields: [
    {
      id: 'field_wh_code',
      code: 'code',
      name: 'Code',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      sortable: true,
      minLength: 2,
      maxLength: 20,
      version: 1,
      active: true,
      placeholder: 'e.g. WH-001',
    },
    {
      id: 'field_wh_name',
      code: 'name',
      name: 'Name',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      sortable: true,
      version: 1,
      active: true,
      placeholder: 'Warehouse name',
    },
    {
      id: 'field_wh_desc',
      code: 'description',
      name: 'Description',
      type: 'TEXTAREA',
      required: false,
      version: 1,
      active: true,
    },
  ],
};

export const warehouseFormView: ViewDefinition = {
  id: 'view_wh_form',
  code: 'warehouse_form',
  modelCode: 'warehouse',
  viewType: ViewType.FORM,
  name: 'Warehouse Form',
  title: 'Warehouse Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'General Info' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'code' } },
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'name' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [{ type: LayoutType.COLUMN, config: { span: 12, fieldCode: 'description' } }],
          },
        ],
      },
    ],
  },
};

export const warehouseGridView: ViewDefinition = {
  id: 'view_wh_grid',
  code: 'warehouse_list',
  modelCode: 'warehouse',
  viewType: ViewType.GRID,
  name: 'Warehouse Grid',
  title: 'Warehouses',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: ['code', 'name', 'isActive'],
    },
  },
};

export const warehouseWorkflow: WorkflowDefinition = {
  id: 'wf_wh_lifecycle',
  code: 'warehouse_workflow',
  name: 'Warehouse Lifecycle',
  modelCode: 'warehouse',
  version: 1,
  active: true,
  states: [
    { code: 'DRAFT', name: 'Draft', initial: true },
    { code: 'ACTIVE', name: 'Active' },
    { code: 'CLOSED', name: 'Closed', final: true },
  ],
  transitions: [
    {
      code: 'activate',
      label: 'Activate Warehouse',
      fromState: 'DRAFT',
      toState: 'ACTIVE',
      permissions: ['manager', 'admin'],
    },
    {
      code: 'close',
      label: 'Close Warehouse',
      fromState: 'ACTIVE',
      toState: 'CLOSED',
      permissions: ['admin'],
    },
  ],
};

export const warehouseActions: ActionDefinition[] = [
  {
    id: 'act_wh_save',
    code: 'save_warehouse',
    name: 'Save',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'save',
  },
];

export const warehousePermissions: PermissionDefinition[] = [
  {
    id: 'perm_wh_read',
    code: 'read_warehouse',
    name: 'Read Warehouse',
    version: 1,
    active: true,
    resource: 'warehouse',
    permissionType: PermissionType.MODULE,
  },
];

export const warehouseBundle: RuntimeMetadataBundle = {
  model: warehouseModel,
  views: [warehouseFormView, warehouseGridView],
  workflow: warehouseWorkflow,
  actions: warehouseActions,
  permissions: warehousePermissions,
};
