import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const assetModel: ModelDefinition = {
  id: 'model_asset_001',
  code: 'asset',
  name: 'Asset',
  description: 'Company asset tracking',
  version: 1,
  active: true,
  tableName: 'assets',
  auditable: true,
  workflowEnabled: true,
  tenantAware: false,
  fields: [
    {
      id: 'field_asset_code',
      code: 'assetCode',
      name: 'Asset Code',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_asset_name',
      code: 'assetName',
      name: 'Asset Name',
      type: 'TEXT',
      required: true,
      searchable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_asset_type',
      code: 'assetType',
      name: 'Type',
      type: 'SELECT',
      required: false,
      filterable: true,
      version: 1,
      active: true,
      properties: {
        options: [
          { label: 'Laptop', value: 'LAPTOP' },
          { label: 'Server', value: 'SERVER' },
          { label: 'Printer', value: 'PRINTER' },
          { label: 'Furniture', value: 'FURNITURE' },
          { label: 'Vehicle', value: 'VEHICLE' },
          { label: 'Other', value: 'OTHER' },
        ],
      },
    },
    {
      id: 'field_asset_pdate',
      code: 'purchaseDate',
      name: 'Purchase Date',
      type: 'DATE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_asset_pcost',
      code: 'purchaseCost',
      name: 'Purchase Cost',
      type: 'DECIMAL',
      required: false,
      version: 1,
      active: true,
      defaultValue: 0,
    },
    {
      id: 'field_asset_cval',
      code: 'currentValue',
      name: 'Current Value',
      type: 'DECIMAL',
      required: false,
      readonly: true,
      version: 1,
      active: true,
      defaultValue: 0,
    },
    {
      id: 'field_asset_assign',
      code: 'assignedTo',
      name: 'Assigned To',
      type: 'MANY_TO_ONE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_asset_loc',
      code: 'location',
      name: 'Location',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_asset_st',
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
          { label: 'Maintenance', value: 'MAINTENANCE' },
          { label: 'Disposed', value: 'DISPOSED' },
        ],
      },
    },
  ],
};

export const assetFormView: ViewDefinition = {
  id: 'view_asset_form',
  code: 'asset_form',
  modelCode: 'asset',
  viewType: ViewType.FORM,
  name: 'Asset Form',
  title: 'Asset Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Asset Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'assetCode' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'assetName' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'assetType' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'status' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'purchaseDate' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'purchaseCost' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'currentValue' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'location' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [{ type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'assignedTo' } }],
          },
        ],
      },
    ],
  },
};

export const assetGridView: ViewDefinition = {
  id: 'view_asset_grid',
  code: 'asset_list',
  modelCode: 'asset',
  viewType: ViewType.GRID,
  name: 'Asset Grid',
  title: 'Assets',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: [
        'assetCode',
        'assetName',
        'assetType',
        'purchaseCost',
        'currentValue',
        'assignedTo',
        'status',
      ],
    },
  },
};

export const assetActions: ActionDefinition[] = [
  {
    id: 'act_asset_create',
    code: 'create_asset',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
  {
    id: 'act_asset_activate',
    code: 'activate',
    name: 'Activate',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'check',
  },
  {
    id: 'act_asset_maintain',
    code: 'maintain',
    name: 'Maintenance',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'tool',
  },
  {
    id: 'act_asset_dispose',
    code: 'dispose',
    name: 'Dispose',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'trash',
  },
];

export const assetPermissions: PermissionDefinition[] = [
  {
    id: 'perm_asset_read',
    code: 'read_asset',
    name: 'Read Asset',
    version: 1,
    active: true,
    resource: 'asset',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_asset_write',
    code: 'write_asset',
    name: 'Write Asset',
    version: 1,
    active: true,
    resource: 'asset',
    permissionType: PermissionType.MODULE,
  },
];

export const assetBundle: RuntimeMetadataBundle = {
  model: assetModel,
  views: [assetFormView, assetGridView],
  actions: assetActions,
  permissions: assetPermissions,
};
