import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RelationType, LoadingStrategy } from '@/core/metadata/schema/relation/RelationDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';
import { WorkflowDefinition } from '@/core/metadata/schema/workflow/WorkflowDefinition';

export const productModel: ModelDefinition = {
  id: 'model_product_001',
  code: 'product',
  name: 'Product',
  description: 'Master data for products and SKUs',
  version: 1,
  active: true,
  tableName: 'products',
  auditable: true,
  workflowEnabled: true,
  tenantAware: false,
  fields: [
    {
      id: 'field_prod_code',
      code: 'code',
      name: 'Code',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      sortable: true,
      minLength: 2,
      maxLength: 50,
      version: 1,
      active: true,
      placeholder: 'e.g. PROD-001',
    },
    {
      id: 'field_prod_name',
      code: 'name',
      name: 'Name',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      sortable: true,
      version: 1,
      active: true,
      placeholder: 'Product name',
    },
    {
      id: 'field_prod_desc',
      code: 'description',
      name: 'Description',
      type: 'TEXTAREA',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_prod_sku',
      code: 'sku',
      name: 'SKU',
      type: 'TEXT',
      required: false,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_prod_barcode',
      code: 'barcode',
      name: 'Barcode',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_prod_uom',
      code: 'uom',
      name: 'UOM',
      type: 'SELECT',
      required: false,
      version: 1,
      active: true,
      defaultValue: 'UNIT',
      properties: {
        options: [
          { label: 'Unit', value: 'UNIT' },
          { label: 'Hour', value: 'HOUR' },
          { label: 'Kilogram', value: 'KG' },
          { label: 'Liter', value: 'LTR' },
          { label: 'Meter', value: 'MTR' },
        ],
      },
    },
    {
      id: 'field_prod_type',
      code: 'productType',
      name: 'Product Type',
      type: 'SELECT',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
      defaultValue: 'ITEM',
      properties: {
        options: [
          { label: 'Item', value: 'ITEM' },
          { label: 'Service', value: 'SERVICE' },
          { label: 'Expense', value: 'EXPENSE' },
          { label: 'Digital', value: 'DIGITAL' },
        ],
      },
    },
    {
      id: 'field_prod_stocked',
      code: 'isStocked',
      name: 'Is Stocked',
      type: 'BOOLEAN',
      required: false,
      version: 1,
      active: true,
      defaultValue: true,
      visibleWhen: {
        '==': [{ var: 'productType' }, 'ITEM'],
      },
    },
    {
      id: 'field_prod_sold',
      code: 'isSold',
      name: 'Is Sold',
      type: 'BOOLEAN',
      required: false,
      version: 1,
      active: true,
      defaultValue: true,
    },
    {
      id: 'field_prod_purchased',
      code: 'isPurchased',
      name: 'Is Purchased',
      type: 'BOOLEAN',
      required: false,
      version: 1,
      active: true,
      defaultValue: false,
    },
    {
      id: 'field_prod_category',
      code: 'categoryId',
      name: 'Category',
      type: 'MANY_TO_ONE',
      required: false,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
      relation: {
        id: 'rel_prod_category',
        code: 'product_category',
        name: 'Product Category',
        version: 1,
        active: true,
        relationType: RelationType.MANY_TO_ONE,
        targetModel: 'product_category',
        displayField: 'name',
        valueField: 'id',
        cascadeSave: false,
        loadingStrategy: LoadingStrategy.LAZY,
      },
    },
  ],
};

export const productFormView: ViewDefinition = {
  id: 'view_product_form',
  code: 'product_form',
  modelCode: 'product',
  viewType: ViewType.FORM,
  name: 'Product Form',
  title: 'Product Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Basic Information' },
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
      {
        type: LayoutType.SECTION,
        config: { title: 'Classification' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'productType' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'categoryId' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'uom' } },
            ],
          },
        ],
      },
      {
        type: LayoutType.SECTION,
        config: { title: 'Identifiers' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'sku' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'barcode' } },
            ],
          },
        ],
      },
      {
        type: LayoutType.SECTION,
        config: { title: 'Configuration' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'isStocked' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'isSold' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'isPurchased' } },
            ],
          },
        ],
      },
    ],
  },
};

export const productGridView: ViewDefinition = {
  id: 'view_product_grid',
  code: 'product_list',
  modelCode: 'product',
  viewType: ViewType.GRID,
  name: 'Product Grid',
  title: 'Products',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: ['code', 'name', 'productType', 'uom', 'isStocked', 'isActive'],
    },
  },
};

export const productWorkflow: WorkflowDefinition = {
  id: 'wf_product_lifecycle',
  code: 'product_workflow',
  name: 'Product Lifecycle',
  modelCode: 'product',
  version: 1,
  active: true,
  states: [
    { code: 'DRAFT', name: 'Draft', initial: true },
    { code: 'ACTIVE', name: 'Active' },
    { code: 'ARCHIVED', name: 'Archived', final: true },
  ],
  transitions: [
    {
      code: 'activate',
      label: 'Activate Product',
      fromState: 'DRAFT',
      toState: 'ACTIVE',
      permissions: ['manager', 'admin'],
    },
    {
      code: 'archive',
      label: 'Archive Product',
      fromState: 'ACTIVE',
      toState: 'ARCHIVED',
      permissions: ['manager', 'admin'],
    },
  ],
};

export const productActions: ActionDefinition[] = [
  {
    id: 'act_prod_save',
    code: 'save_product',
    name: 'Save',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'save',
  },
  {
    id: 'act_prod_delete',
    code: 'delete_product',
    name: 'Delete',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'delete',
    enabledWhen: {
      '==': [{ var: 'status' }, 'DRAFT'],
    },
  },
];

export const productPermissions: PermissionDefinition[] = [
  {
    id: 'perm_prod_read',
    code: 'read_product',
    name: 'Read Product',
    version: 1,
    active: true,
    resource: 'product',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_prod_write',
    code: 'write_product',
    name: 'Write Product',
    version: 1,
    active: true,
    resource: 'product',
    permissionType: PermissionType.MODULE,
  },
];

export const productBundle: RuntimeMetadataBundle = {
  model: productModel,
  views: [productFormView, productGridView],
  workflow: productWorkflow,
  actions: productActions,
  permissions: productPermissions,
};
