import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const manufacturingOrderModel: ModelDefinition = {
  id: 'model_mo_001',
  code: 'manufacturing_order',
  name: 'Manufacturing Order',
  description: 'Production execution order',
  version: 1,
  active: true,
  tableName: 'manufacturing_orders',
  auditable: true,
  workflowEnabled: true,
  tenantAware: false,
  fields: [
    {
      id: 'field_mo_docno',
      code: 'documentNo',
      name: 'Document No',
      type: 'TEXT',
      required: true,
      readonly: true,
      searchable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_mo_product',
      code: 'productId',
      name: 'Product',
      type: 'MANY_TO_ONE',
      required: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_mo_bom',
      code: 'bomId',
      name: 'BOM',
      type: 'MANY_TO_ONE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_mo_routing',
      code: 'routingId',
      name: 'Routing',
      type: 'MANY_TO_ONE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_mo_warehouse',
      code: 'warehouseId',
      name: 'Warehouse',
      type: 'MANY_TO_ONE',
      required: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_mo_planned_qty',
      code: 'plannedQuantity',
      name: 'Planned Qty',
      type: 'DECIMAL',
      required: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_mo_completed_qty',
      code: 'completedQuantity',
      name: 'Completed Qty',
      type: 'DECIMAL',
      required: false,
      readonly: true,
      version: 1,
      active: true,
      defaultValue: 0,
    },
    {
      id: 'field_mo_status',
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
          { label: 'Planned', value: 'PLANNED' },
          { label: 'Released', value: 'RELEASED' },
          { label: 'In Production', value: 'IN_PRODUCTION' },
          { label: 'Completed', value: 'COMPLETED' },
          { label: 'Closed', value: 'CLOSED' },
        ],
      },
    },
    {
      id: 'field_mo_priority',
      code: 'priority',
      name: 'Priority',
      type: 'SELECT',
      required: false,
      version: 1,
      active: true,
      defaultValue: 'MEDIUM',
      properties: {
        options: [
          { label: 'Low', value: 'LOW' },
          { label: 'Medium', value: 'MEDIUM' },
          { label: 'High', value: 'HIGH' },
          { label: 'Critical', value: 'CRITICAL' },
        ],
      },
    },
    {
      id: 'field_mo_start',
      code: 'plannedStart',
      name: 'Start Date',
      type: 'DATE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_mo_end',
      code: 'plannedEnd',
      name: 'End Date',
      type: 'DATE',
      required: false,
      version: 1,
      active: true,
    },
  ],
};

export const manufacturingOrderFormView: ViewDefinition = {
  id: 'view_mo_form',
  code: 'mo_form',
  modelCode: 'manufacturing_order',
  viewType: ViewType.FORM,
  name: 'MO Form',
  title: 'Manufacturing Order Details',
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
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'documentNo' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'productId' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'status' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'priority' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'bomId' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'routingId' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'warehouseId' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'plannedQuantity' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'completedQuantity' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'plannedStart' } },
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'plannedEnd' } },
            ],
          },
        ],
      },
    ],
  },
};

export const manufacturingOrderGridView: ViewDefinition = {
  id: 'view_mo_grid',
  code: 'mo_list',
  modelCode: 'manufacturing_order',
  viewType: ViewType.GRID,
  name: 'MO Grid',
  title: 'Manufacturing Orders',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: [
        'documentNo',
        'productId',
        'plannedQuantity',
        'completedQuantity',
        'status',
        'priority',
        'plannedStart',
        'plannedEnd',
      ],
    },
  },
};

export const manufacturingOrderActions: ActionDefinition[] = [
  {
    id: 'act_mo_create',
    code: 'create_mo',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
  {
    id: 'act_mo_plan',
    code: 'plan',
    name: 'Plan',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'plan',
  },
  {
    id: 'act_mo_release',
    code: 'release',
    name: 'Release',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'send',
  },
  {
    id: 'act_mo_start',
    code: 'start',
    name: 'Start',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'play',
  },
  {
    id: 'act_mo_complete',
    code: 'complete',
    name: 'Complete',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'check',
  },
  {
    id: 'act_mo_close',
    code: 'close',
    name: 'Close',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'lock',
  },
  {
    id: 'act_mo_print',
    code: 'print',
    name: 'Print',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'print',
  },
];

export const manufacturingOrderPermissions: PermissionDefinition[] = [
  {
    id: 'perm_mo_read',
    code: 'read_mo',
    name: 'Read Manufacturing Order',
    version: 1,
    active: true,
    resource: 'manufacturing_order',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_mo_write',
    code: 'write_mo',
    name: 'Write Manufacturing Order',
    version: 1,
    active: true,
    resource: 'manufacturing_order',
    permissionType: PermissionType.MODULE,
  },
];

export const manufacturingOrderBundle: RuntimeMetadataBundle = {
  model: manufacturingOrderModel,
  views: [manufacturingOrderFormView, manufacturingOrderGridView],
  actions: manufacturingOrderActions,
  permissions: manufacturingOrderPermissions,
};
