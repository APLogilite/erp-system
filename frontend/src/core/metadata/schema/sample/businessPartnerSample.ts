import { ActionDefinition, ActionType } from '../action/ActionDefinition';
import { LayoutType } from '../layout/LayoutDefinition';
import { ModelDefinition } from '../model/ModelDefinition';
import { PermissionDefinition, PermissionType } from '../permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '../RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '../view/ViewDefinition';
import { WorkflowDefinition } from '../workflow/WorkflowDefinition';

/**
 * Reference Model Definition for business_partner
 */
export const businessPartnerModel: ModelDefinition = {
  id: 'model_bp_001',
  code: 'business_partner',
  name: 'Business Partner',
  description: 'Master data for customers, vendors, and employees',
  version: 1,
  active: true,
  tableName: 'sys_business_partners',
  auditable: true,
  workflowEnabled: true,
  tenantAware: true,
  fields: [
    {
      id: 'field_bp_code',
      code: 'code',
      name: 'Code',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      sortable: true,
      minLength: 3,
      maxLength: 50,
      version: 1,
      active: true,
      placeholder: 'e.g. BP0001',
    },
    {
      id: 'field_bp_name',
      code: 'name',
      name: 'Name',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      sortable: true,
      version: 1,
      active: true,
      placeholder: 'Full name or company name',
    },
    {
      id: 'field_bp_type',
      code: 'partnerType',
      name: 'Partner Type',
      type: 'SELECT',
      required: true,
      searchable: true,
      filterable: true,
      sortable: true,
      version: 1,
      active: true,
      defaultValue: 'CUSTOMER',
      properties: {
        options: [
          { label: 'Customer', value: 'CUSTOMER' },
          { label: 'Vendor', value: 'VENDOR' },
          { label: 'Employee', value: 'EMPLOYEE' },
        ],
      },
    },
    {
      id: 'field_bp_email',
      code: 'email',
      name: 'Email',
      type: 'EMAIL',
      required: false,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
      placeholder: 'name@company.com',
    },
    {
      id: 'field_bp_phone',
      code: 'phone',
      name: 'Phone',
      type: 'PHONE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_bp_credit_limit',
      code: 'credit_limit',
      name: 'Credit Limit',
      type: 'DECIMAL',
      required: false,
      version: 1,
      active: true,
      defaultValue: 0.0,
      visibleWhen: {
        '==': [{ var: 'partnerType' }, 'CUSTOMER'],
      },
    },
    {
      id: 'field_bp_payment_terms',
      code: 'payment_terms',
      name: 'Payment Terms',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
      placeholder: 'e.g. Net 30',
      visibleWhen: {
        in: [{ var: 'partnerType' }, ['CUSTOMER', 'VENDOR']],
      },
    },
    {
      id: 'field_bp_is_active',
      code: 'isActive',
      name: 'Active Status',
      type: 'BOOLEAN',
      required: true,
      version: 1,
      active: true,
      defaultValue: true,
    },
  ],
};

/**
 * Reference Form View Definition for business_partner
 */
export const businessPartnerFormView: ViewDefinition = {
  id: 'view_bp_form',
  code: 'business_partner_form',
  modelCode: 'business_partner',
  viewType: ViewType.FORM,
  name: 'Business Partner Form',
  title: 'Business Partner Details',
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
              {
                type: LayoutType.COLUMN,
                config: { span: 6, fieldCode: 'code' },
              },
              {
                type: LayoutType.COLUMN,
                config: { span: 6, fieldCode: 'name' },
              },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              {
                type: LayoutType.COLUMN,
                config: { span: 6, fieldCode: 'partnerType' },
              },
              {
                type: LayoutType.COLUMN,
                config: { span: 6, fieldCode: 'isActive' },
              },
            ],
          },
        ],
      },
      {
        type: LayoutType.SECTION,
        config: { title: 'Contact & Financial Details' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              {
                type: LayoutType.COLUMN,
                config: { span: 6, fieldCode: 'email' },
              },
              {
                type: LayoutType.COLUMN,
                config: { span: 6, fieldCode: 'phone' },
              },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              {
                type: LayoutType.COLUMN,
                config: { span: 6, fieldCode: 'credit_limit' },
              },
              {
                type: LayoutType.COLUMN,
                config: { span: 6, fieldCode: 'payment_terms' },
              },
            ],
          },
        ],
      },
    ],
  },
};

/**
 * Reference Grid View Definition for business_partner
 */
export const businessPartnerGridView: ViewDefinition = {
  id: 'view_bp_grid',
  code: 'business_partner_list',
  modelCode: 'business_partner',
  viewType: ViewType.GRID,
  name: 'Business Partner Grid',
  title: 'Business Partners',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: ['code', 'name', 'partnerType', 'email', 'isActive'],
    },
  },
};

/**
 * Reference Workflow Definition for business_partner
 */
export const businessPartnerWorkflow: WorkflowDefinition = {
  id: 'wf_bp_lifecycle',
  code: 'business_partner_workflow',
  name: 'Business Partner Lifecycle',
  modelCode: 'business_partner',
  version: 1,
  active: true,
  states: [
    { code: 'draft', name: 'Draft', initial: true },
    { code: 'active', name: 'Active' },
    { code: 'inactive', name: 'Suspended', final: true },
  ],
  transitions: [
    {
      code: 'activate',
      label: 'Activate Partner',
      fromState: 'draft',
      toState: 'active',
      permissions: ['manager', 'admin'],
    },
    {
      code: 'suspend',
      label: 'Suspend Partner',
      fromState: 'active',
      toState: 'inactive',
      permissions: ['manager', 'admin'],
      guardExpression: {
        '==': [{ var: 'isActive' }, false],
      },
    },
    {
      code: 'reactivate',
      label: 'Reactivate Partner',
      fromState: 'inactive',
      toState: 'active',
      permissions: ['admin'],
    },
  ],
};

/**
 * Reference Actions for business_partner
 */
export const businessPartnerActions: ActionDefinition[] = [
  {
    id: 'act_bp_save',
    code: 'save_business_partner',
    name: 'Save',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'save',
  },
  {
    id: 'act_bp_delete',
    code: 'delete_business_partner',
    name: 'Delete',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'delete',
    enabledWhen: {
      '==': [{ var: 'status' }, 'draft'],
    },
  },
];

/**
 * Reference Permissions for business_partner
 */
export const businessPartnerPermissions: PermissionDefinition[] = [
  {
    id: 'perm_bp_read',
    code: 'read_business_partner',
    name: 'Read Business Partner',
    version: 1,
    active: true,
    resource: 'business_partner',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_bp_limit_edit',
    code: 'edit_credit_limit',
    name: 'Edit Credit Limit',
    version: 1,
    active: true,
    resource: 'business_partner.credit_limit',
    permissionType: PermissionType.FIELD,
    expression: {
      in: ['admin', { var: 'user.roles' }],
    },
  },
];

/**
 * Complete Metadata Bundle for reference
 */
export const businessPartnerBundle: RuntimeMetadataBundle = {
  model: businessPartnerModel,
  views: [businessPartnerFormView, businessPartnerGridView],
  workflow: businessPartnerWorkflow,
  actions: businessPartnerActions,
  permissions: businessPartnerPermissions,
};
