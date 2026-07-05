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

export const inventoryTransactionModel: ModelDefinition = {
  id: 'model_invtx_001',
  code: 'inventory_transaction',
  name: 'Inventory Transaction',
  description: 'Stock movement tracking document',
  version: 1,
  active: true,
  tableName: 'inventory_transactions',
  auditable: true,
  workflowEnabled: true,
  tenantAware: false,
  fields: [
    {
      id: 'field_invtx_docno',
      code: 'documentNumber',
      name: 'Document No',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      sortable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_invtx_wh',
      code: 'warehouseId',
      name: 'Warehouse',
      type: 'MANY_TO_ONE',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_invtx_type',
      code: 'transactionType',
      name: 'Type',
      type: 'SELECT',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
      properties: {
        options: [
          { label: 'In', value: 'IN' },
          { label: 'Out', value: 'OUT' },
          { label: 'Transfer', value: 'TRANSFER' },
          { label: 'Adjustment', value: 'ADJUSTMENT' },
        ],
      },
    },
    {
      id: 'field_invtx_date',
      code: 'transactionDate',
      name: 'Transaction Date',
      type: 'DATE',
      required: true,
      searchable: true,
      filterable: true,
      sortable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_invtx_status',
      code: 'status',
      name: 'Status',
      type: 'SELECT',
      required: false,
      version: 1,
      active: true,
      defaultValue: 'DRAFT',
      properties: {
        options: [
          { label: 'Draft', value: 'DRAFT' },
          { label: 'Completed', value: 'COMPLETED' },
          { label: 'Posted', value: 'POSTED' },
          { label: 'Closed', value: 'CLOSED' },
        ],
      },
    },
    {
      id: 'field_invtx_desc',
      code: 'description',
      name: 'Description',
      type: 'TEXTAREA',
      required: false,
      version: 1,
      active: true,
    },
  ],
};

export const inventoryTransactionFormView: ViewDefinition = {
  id: 'view_invtx_form',
  code: 'inventory_transaction_form',
  modelCode: 'inventory_transaction',
  viewType: ViewType.FORM,
  name: 'Inventory Transaction Form',
  title: 'Inventory Transaction Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Transaction Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'documentNumber' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'transactionDate' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'status' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'warehouseId' } },
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'transactionType' } },
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

export const inventoryTransactionGridView: ViewDefinition = {
  id: 'view_invtx_grid',
  code: 'inventory_transaction_list',
  modelCode: 'inventory_transaction',
  viewType: ViewType.GRID,
  name: 'Inventory Transaction Grid',
  title: 'Inventory Transactions',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: ['documentNumber', 'transactionDate', 'warehouseId', 'transactionType', 'status'],
    },
  },
};

export const inventoryTransactionWorkflow: WorkflowDefinition = {
  id: 'wf_invtx_workflow',
  code: 'inventory_transaction_workflow',
  name: 'Inventory Transaction Workflow',
  modelCode: 'inventory_transaction',
  version: 1,
  active: true,
  states: [
    { code: 'DRAFT', name: 'Draft', initial: true },
    { code: 'COMPLETED', name: 'Completed' },
    { code: 'POSTED', name: 'Posted' },
    { code: 'CLOSED', name: 'Closed', final: true },
  ],
  transitions: [
    {
      code: 'complete',
      label: 'Complete',
      fromState: 'DRAFT',
      toState: 'COMPLETED',
      permissions: ['inventory_user', 'inventory_manager'],
    },
    {
      code: 'post',
      label: 'Post',
      fromState: 'COMPLETED',
      toState: 'POSTED',
      permissions: ['inventory_manager'],
    },
    {
      code: 'close',
      label: 'Close',
      fromState: 'POSTED',
      toState: 'CLOSED',
      permissions: ['inventory_manager', 'admin'],
    },
    {
      code: 'void',
      label: 'Void',
      fromState: 'DRAFT',
      toState: 'CLOSED',
      permissions: ['inventory_manager', 'admin'],
    },
  ],
};

export const inventoryTransactionActions: ActionDefinition[] = [
  {
    id: 'act_invtx_save',
    code: 'save_inventory_transaction',
    name: 'Save',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'save',
  },
  {
    id: 'act_invtx_delete',
    code: 'delete_inventory_transaction',
    name: 'Delete',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'delete',
    enabledWhen: { '==': [{ var: 'status' }, 'DRAFT'] },
  },
  {
    id: 'act_invtx_complete',
    code: 'complete',
    name: 'Complete',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'check_circle',
  },
  {
    id: 'act_invtx_post',
    code: 'post',
    name: 'Post',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'publish',
  },
  {
    id: 'act_invtx_close',
    code: 'close',
    name: 'Close',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'lock',
  },
  {
    id: 'act_invtx_print',
    code: 'print',
    name: 'Print',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'print',
  },
];

export const inventoryTransactionPermissions: PermissionDefinition[] = [
  {
    id: 'perm_invtx_read',
    code: 'read_inventory_transaction',
    name: 'Read Inventory Transaction',
    version: 1,
    active: true,
    resource: 'inventory_transaction',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_invtx_write',
    code: 'write_inventory_transaction',
    name: 'Write Inventory Transaction',
    version: 1,
    active: true,
    resource: 'inventory_transaction',
    permissionType: PermissionType.MODULE,
  },
];

export const inventoryTransactionBundle: RuntimeMetadataBundle = {
  model: inventoryTransactionModel,
  views: [inventoryTransactionFormView, inventoryTransactionGridView],
  workflow: inventoryTransactionWorkflow,
  actions: inventoryTransactionActions,
  permissions: inventoryTransactionPermissions,
};
