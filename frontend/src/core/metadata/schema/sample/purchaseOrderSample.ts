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

export const purchaseOrderModel: ModelDefinition = {
  id: 'model_po_001',
  code: 'purchase_order',
  name: 'Purchase Order',
  description: 'Vendor procurement orders',
  version: 1,
  active: true,
  tableName: 'purchase_orders',
  auditable: true,
  workflowEnabled: true,
  tenantAware: false,
  fields: [
    {
      id: 'field_po_docno',
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
      id: 'field_po_date',
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
      id: 'field_po_vendor',
      code: 'vendorId',
      name: 'Vendor',
      type: 'MANY_TO_ONE',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_po_warehouse',
      code: 'warehouseId',
      name: 'Warehouse',
      type: 'MANY_TO_ONE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_po_status',
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
          { label: 'Received', value: 'RECEIVED' },
          { label: 'Closed', value: 'CLOSED' },
          { label: 'Void', value: 'VOID' },
        ],
      },
    },
    {
      id: 'field_po_desc',
      code: 'description',
      name: 'Description',
      type: 'TEXTAREA',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_po_currency',
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
    {
      id: 'field_po_total',
      code: 'totalAmount',
      name: 'Total Amount',
      type: 'DECIMAL',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_po_expected',
      code: 'expectedDate',
      name: 'Expected Date',
      type: 'DATE',
      required: false,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
  ],
};

export const purchaseOrderFormView: ViewDefinition = {
  id: 'view_po_form',
  code: 'purchase_order_form',
  modelCode: 'purchase_order',
  viewType: ViewType.FORM,
  name: 'Purchase Order Form',
  title: 'Purchase Order Details',
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
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'vendorId' } },
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
        config: { title: 'Schedule & Financial' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'expectedDate' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'currency' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'totalAmount' } },
            ],
          },
        ],
      },
    ],
  },
};

export const purchaseOrderGridView: ViewDefinition = {
  id: 'view_po_grid',
  code: 'purchase_order_list',
  modelCode: 'purchase_order',
  viewType: ViewType.GRID,
  name: 'Purchase Order Grid',
  title: 'Purchase Orders',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: ['documentNo', 'documentDate', 'vendorId', 'status', 'totalAmount', 'expectedDate'],
    },
  },
};

export const purchaseOrderWorkflow: WorkflowDefinition = {
  id: 'wf_po_workflow',
  code: 'purchase_order_workflow',
  name: 'Purchase Order Workflow',
  modelCode: 'purchase_order',
  version: 1,
  active: true,
  states: [
    { code: 'DRAFT', name: 'Draft', initial: true },
    { code: 'COMPLETED', name: 'Completed' },
    { code: 'APPROVED', name: 'Approved' },
    { code: 'RECEIVED', name: 'Received' },
    { code: 'CLOSED', name: 'Closed', final: true },
  ],
  transitions: [
    {
      code: 'complete',
      label: 'Complete',
      fromState: 'DRAFT',
      toState: 'COMPLETED',
      permissions: ['purchase_user', 'purchase_manager'],
    },
    {
      code: 'approve',
      label: 'Approve',
      fromState: 'COMPLETED',
      toState: 'APPROVED',
      permissions: ['purchase_manager'],
    },
    {
      code: 'receive',
      label: 'Receive',
      fromState: 'APPROVED',
      toState: 'RECEIVED',
      permissions: ['purchase_manager'],
    },
    {
      code: 'close',
      label: 'Close',
      fromState: 'RECEIVED',
      toState: 'CLOSED',
      permissions: ['purchase_manager', 'admin'],
    },
    {
      code: 'void',
      label: 'Void',
      fromState: 'DRAFT',
      toState: 'CLOSED',
      permissions: ['purchase_manager', 'admin'],
    },
    {
      code: 'reopen',
      label: 'Reopen',
      fromState: 'CLOSED',
      toState: 'DRAFT',
      permissions: ['admin'],
    },
  ],
};

export const purchaseOrderActions: ActionDefinition[] = [
  {
    id: 'act_po_save',
    code: 'save_purchase_order',
    name: 'Save',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'save',
  },
  {
    id: 'act_po_delete',
    code: 'delete_purchase_order',
    name: 'Delete',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'delete',
    enabledWhen: { '==': [{ var: 'status' }, 'DRAFT'] },
  },
  {
    id: 'act_po_add_line',
    code: 'add_line',
    name: 'Add Line',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'add',
  },
  {
    id: 'act_po_recalc',
    code: 'recalculate',
    name: 'Recalculate',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'calculate',
  },
  {
    id: 'act_po_complete',
    code: 'complete',
    name: 'Complete',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'check_circle',
  },
  {
    id: 'act_po_approve',
    code: 'approve',
    name: 'Approve',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'approval',
  },
  {
    id: 'act_po_receive',
    code: 'receive',
    name: 'Receive',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'inventory',
  },
  {
    id: 'act_po_close',
    code: 'close',
    name: 'Close',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'lock',
  },
  {
    id: 'act_po_print',
    code: 'print',
    name: 'Print',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'print',
  },
];

export const purchaseOrderPermissions: PermissionDefinition[] = [
  {
    id: 'perm_po_read',
    code: 'read_purchase_order',
    name: 'Read Purchase Order',
    version: 1,
    active: true,
    resource: 'purchase_order',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_po_write',
    code: 'write_purchase_order',
    name: 'Write Purchase Order',
    version: 1,
    active: true,
    resource: 'purchase_order',
    permissionType: PermissionType.MODULE,
  },
];

export const purchaseOrderBundle: RuntimeMetadataBundle = {
  model: purchaseOrderModel,
  views: [purchaseOrderFormView, purchaseOrderGridView],
  workflow: purchaseOrderWorkflow,
  actions: purchaseOrderActions,
  permissions: purchaseOrderPermissions,
};
