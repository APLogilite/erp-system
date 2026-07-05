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

export const salesOrderModel: ModelDefinition = {
  id: 'model_so_001',
  code: 'sales_order',
  name: 'Sales Order',
  description: 'Customer purchase orders',
  version: 1,
  active: true,
  tableName: 'sales_orders',
  auditable: true,
  workflowEnabled: true,
  tenantAware: false,
  fields: [
    {
      id: 'field_so_docno',
      code: 'documentNo',
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
      id: 'field_so_date',
      code: 'documentDate',
      name: 'Document Date',
      type: 'DATE',
      required: true,
      searchable: true,
      filterable: true,
      sortable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_so_customer',
      code: 'customerId',
      name: 'Customer',
      type: 'MANY_TO_ONE',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_so_warehouse',
      code: 'warehouseId',
      name: 'Warehouse',
      type: 'MANY_TO_ONE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_so_status',
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
          { label: 'Approved', value: 'APPROVED' },
          { label: 'Closed', value: 'CLOSED' },
          { label: 'Void', value: 'VOID' },
        ],
      },
    },
    {
      id: 'field_so_desc',
      code: 'description',
      name: 'Description',
      type: 'TEXTAREA',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_so_total',
      code: 'totalAmount',
      name: 'Total Amount',
      type: 'DECIMAL',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_so_currency',
      code: 'currency',
      name: 'Currency',
      type: 'SELECT',
      required: false,
      version: 1,
      active: true,
      defaultValue: 'USD',
      properties: {
        options: [
          { label: 'USD', value: 'USD' },
          { label: 'EUR', value: 'EUR' },
          { label: 'INR', value: 'INR' },
        ],
      },
    },
  ],
};

export const salesOrderFormView: ViewDefinition = {
  id: 'view_so_form',
  code: 'sales_order_form',
  modelCode: 'sales_order',
  viewType: ViewType.FORM,
  name: 'Sales Order Form',
  title: 'Sales Order Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Order Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'documentNo' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'documentDate' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'status' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'customerId' } },
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'warehouseId' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [{ type: LayoutType.COLUMN, config: { span: 12, fieldCode: 'description' } }],
          },
        ],
      },
      {
        type: LayoutType.SECTION,
        config: { title: 'Financial Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'totalAmount' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'currency' } },
            ],
          },
        ],
      },
    ],
  },
};

export const salesOrderGridView: ViewDefinition = {
  id: 'view_so_grid',
  code: 'sales_order_list',
  modelCode: 'sales_order',
  viewType: ViewType.GRID,
  name: 'Sales Order Grid',
  title: 'Sales Orders',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: ['documentNo', 'documentDate', 'customerId', 'status', 'totalAmount', 'currency'],
    },
  },
};

export const salesOrderWorkflow: WorkflowDefinition = {
  id: 'wf_so_workflow',
  code: 'sales_order_workflow',
  name: 'Sales Order Workflow',
  modelCode: 'sales_order',
  version: 1,
  active: true,
  states: [
    { code: 'DRAFT', name: 'Draft', initial: true },
    { code: 'COMPLETED', name: 'Completed' },
    { code: 'APPROVED', name: 'Approved' },
    { code: 'CLOSED', name: 'Closed', final: true },
  ],
  transitions: [
    {
      code: 'complete',
      label: 'Complete',
      fromState: 'DRAFT',
      toState: 'COMPLETED',
      permissions: ['sales_user', 'sales_manager'],
    },
    {
      code: 'approve',
      label: 'Approve',
      fromState: 'COMPLETED',
      toState: 'APPROVED',
      permissions: ['sales_manager'],
    },
    {
      code: 'close',
      label: 'Close',
      fromState: 'APPROVED',
      toState: 'CLOSED',
      permissions: ['sales_manager', 'admin'],
    },
    {
      code: 'reopen',
      label: 'Reopen',
      fromState: 'CLOSED',
      toState: 'DRAFT',
      permissions: ['admin'],
    },
    {
      code: 'void',
      label: 'Void',
      fromState: 'DRAFT',
      toState: 'CLOSED',
      permissions: ['sales_manager', 'admin'],
    },
  ],
};

export const salesOrderActions: ActionDefinition[] = [
  {
    id: 'act_so_save',
    code: 'save_sales_order',
    name: 'Save',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'save',
  },
  {
    id: 'act_so_delete',
    code: 'delete_sales_order',
    name: 'Delete',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'delete',
    enabledWhen: { '==': [{ var: 'status' }, 'DRAFT'] },
  },
  {
    id: 'act_so_add_line',
    code: 'add_line',
    name: 'Add Line',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'add',
  },
  {
    id: 'act_so_recalc',
    code: 'recalculate',
    name: 'Recalculate',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'calculate',
  },
  {
    id: 'act_so_complete',
    code: 'complete',
    name: 'Complete',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'check_circle',
  },
  {
    id: 'act_so_approve',
    code: 'approve',
    name: 'Approve',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'approval',
  },
  {
    id: 'act_so_close',
    code: 'close',
    name: 'Close',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'lock',
  },
  {
    id: 'act_so_print',
    code: 'print',
    name: 'Print',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'print',
  },
];

export const salesOrderPermissions: PermissionDefinition[] = [
  {
    id: 'perm_so_read',
    code: 'read_sales_order',
    name: 'Read Sales Order',
    version: 1,
    active: true,
    resource: 'sales_order',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_so_write',
    code: 'write_sales_order',
    name: 'Write Sales Order',
    version: 1,
    active: true,
    resource: 'sales_order',
    permissionType: PermissionType.MODULE,
  },
];

export const salesOrderBundle: RuntimeMetadataBundle = {
  model: salesOrderModel,
  views: [salesOrderFormView, salesOrderGridView],
  workflow: salesOrderWorkflow,
  actions: salesOrderActions,
  permissions: salesOrderPermissions,
};
