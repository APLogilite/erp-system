import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const reservationModel: ModelDefinition = {
  id: 'model_res_001',
  code: 'reservation',
  name: 'Reservation',
  description: 'Inventory reservation tracking',
  version: 1,
  active: true,
  tableName: 'reservations',
  auditable: true,
  workflowEnabled: true,
  tenantAware: false,
  fields: [
    {
      id: 'field_res_product',
      code: 'productId',
      name: 'Product',
      type: 'MANY_TO_ONE',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_res_warehouse',
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
      id: 'field_res_location',
      code: 'locationId',
      name: 'Location',
      type: 'MANY_TO_ONE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_res_qty',
      code: 'quantity',
      name: 'Quantity',
      type: 'DECIMAL',
      required: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_res_reserved',
      code: 'reservedQuantity',
      name: 'Reserved Qty',
      type: 'DECIMAL',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_res_source',
      code: 'sourceDocument',
      name: 'Source Document',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_res_status',
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
          { label: 'Reserved', value: 'RESERVED' },
          { label: 'Released', value: 'RELEASED' },
          { label: 'Consumed', value: 'CONSUMED' },
          { label: 'Cancelled', value: 'CANCELLED' },
        ],
      },
    },
  ],
};

export const reservationFormView: ViewDefinition = {
  id: 'view_res_form',
  code: 'reservation_form',
  modelCode: 'reservation',
  viewType: ViewType.FORM,
  name: 'Reservation Form',
  title: 'Reservation Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Reservation Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'productId' } },
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'warehouseId' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'quantity' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'reservedQuantity' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'status' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'locationId' } },
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'sourceDocument' } },
            ],
          },
        ],
      },
    ],
  },
};

export const reservationGridView: ViewDefinition = {
  id: 'view_res_grid',
  code: 'reservation_list',
  modelCode: 'reservation',
  viewType: ViewType.GRID,
  name: 'Reservation Grid',
  title: 'Reservations',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: [
        'productId',
        'warehouseId',
        'quantity',
        'reservedQuantity',
        'status',
        'sourceDocument',
      ],
    },
  },
};

export const reservationActions: ActionDefinition[] = [
  {
    id: 'act_res_create',
    code: 'create_reservation',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
  {
    id: 'act_res_release',
    code: 'release',
    name: 'Release',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'release',
  },
  {
    id: 'act_res_consume',
    code: 'consume',
    name: 'Consume',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'check',
  },
];

export const reservationPermissions: PermissionDefinition[] = [
  {
    id: 'perm_res_read',
    code: 'read_reservation',
    name: 'Read Reservation',
    version: 1,
    active: true,
    resource: 'reservation',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_res_write',
    code: 'write_reservation',
    name: 'Write Reservation',
    version: 1,
    active: true,
    resource: 'reservation',
    permissionType: PermissionType.MODULE,
  },
];

export const reservationBundle: RuntimeMetadataBundle = {
  model: reservationModel,
  views: [reservationFormView, reservationGridView],
  actions: reservationActions,
  permissions: reservationPermissions,
};
