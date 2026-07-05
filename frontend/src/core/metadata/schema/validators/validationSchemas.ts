import { z } from 'zod';

import { ActionType } from '../action/ActionDefinition';
import { LayoutType, LayoutDefinition } from '../layout/LayoutDefinition';
import { PermissionType } from '../permission/PermissionDefinition';
import { RelationType, LoadingStrategy } from '../relation/RelationDefinition';
import { ViewType } from '../view/ViewDefinition';

/**
 * Zod validation schema for BaseMetadata properties
 */
export const BaseMetadataSchema = z.object({
  id: z
    .string()
    .optional()
    .default(() => Math.random().toString(36).substring(7)),
  code: z.string(),
  name: z.string(),
  description: z.string().optional(),
  version: z.number().optional().default(1),
  active: z.boolean().optional().default(true),
  properties: z.record(z.string(), z.unknown()).optional(),
});

/**
 * Zod validation schema for RelationDefinition
 */
export const RelationDefinitionSchema = BaseMetadataSchema.extend({
  relationType: z.nativeEnum(RelationType),
  targetModel: z.string(),
  displayField: z.string(),
  valueField: z.string(),
  cascadeSave: z.boolean().optional().default(false),
  loadingStrategy: z.nativeEnum(LoadingStrategy).optional().default(LoadingStrategy.LAZY),
});

/**
 * Zod validation schema for FieldDefinition
 */
export const FieldDefinitionSchema = BaseMetadataSchema.extend({
  type: z.enum([
    'TEXT',
    'TEXTAREA',
    'NUMBER',
    'DECIMAL',
    'BOOLEAN',
    'DATE',
    'DATETIME',
    'EMAIL',
    'PHONE',
    'SELECT',
    'MULTI_SELECT',
    'MANY_TO_ONE',
    'ONE_TO_MANY',
    'MANY_TO_MANY',
    'TREE',
    'JSON',
  ]),
  required: z.boolean().optional().default(false),
  readonly: z.boolean().optional().default(false),
  hidden: z.boolean().optional().default(false),
  defaultValue: z.unknown().optional(),
  searchable: z.boolean().optional().default(true),
  filterable: z.boolean().optional().default(true),
  sortable: z.boolean().optional().default(true),
  minLength: z.number().optional(),
  maxLength: z.number().optional(),
  minValue: z.number().optional(),
  maxValue: z.number().optional(),
  pattern: z.string().optional(),
  placeholder: z.string().optional(),
  helperText: z.string().optional(),
  visibleWhen: z.unknown().optional(),
  readonlyWhen: z.unknown().optional(),
  requiredWhen: z.unknown().optional(),
  relation: z.lazy(() => RelationDefinitionSchema.optional()),
});

/**
 * Zod validation schema for ModelDefinition
 */
export const ModelDefinitionSchema = BaseMetadataSchema.extend({
  tableName: z.string(),
  auditable: z.boolean().optional().default(true),
  workflowEnabled: z.boolean().optional().default(false),
  tenantAware: z.boolean().optional().default(true),
  fields: z.array(z.lazy(() => FieldDefinitionSchema)),
});

/**
 * Zod validation schema for LayoutDefinition (Recursive Layout Tree)
 */
export const LayoutDefinitionSchema: z.ZodType<LayoutDefinition> = z.lazy(() =>
  z.object({
    type: z.nativeEnum(LayoutType),
    children: z.array(LayoutDefinitionSchema).optional(),
    config: z.record(z.string(), z.unknown()).optional(),
  })
);

/**
 * Zod validation schema for ViewDefinition
 */
export const ViewDefinitionSchema = BaseMetadataSchema.extend({
  modelCode: z.string(),
  viewType: z.nativeEnum(ViewType),
  title: z.string(),
  layout: z.lazy(() => LayoutDefinitionSchema),
});

/**
 * Zod validation schema for JSON Logic expression definition
 */
export const ExpressionDefinitionSchema = z.union([z.record(z.string(), z.unknown()), z.string()]);

/**
 * Zod validation schemas for Workflow components
 */
export const WorkflowStateSchema = z.object({
  code: z.string(),
  name: z.string(),
  initial: z.boolean().optional().default(false),
  final: z.boolean().optional().default(false),
});

export const WorkflowTransitionSchema = z.object({
  code: z.string(),
  label: z.string().optional(),
  fromState: z.string(),
  toState: z.string(),
  guardExpression: ExpressionDefinitionSchema.optional(),
  actions: z.array(z.string()).optional(),
  permissions: z.array(z.string()).optional(),
});

export const WorkflowDefinitionSchema = BaseMetadataSchema.extend({
  modelCode: z.string(),
  states: z.array(WorkflowStateSchema),
  transitions: z.array(WorkflowTransitionSchema),
});

/**
 * Zod validation schema for ActionDefinition
 */
export const ActionDefinitionSchema = BaseMetadataSchema.extend({
  actionType: z.nativeEnum(ActionType),
  icon: z.string().optional(),
  visibleWhen: ExpressionDefinitionSchema.optional(),
  enabledWhen: ExpressionDefinitionSchema.optional(),
});

/**
 * Zod validation schema for PermissionDefinition
 */
export const PermissionDefinitionSchema = BaseMetadataSchema.extend({
  resource: z.string(),
  permissionType: z.nativeEnum(PermissionType),
  expression: ExpressionDefinitionSchema.optional(),
});

/**
 * Zod validation schema for RuntimeMetadataBundle
 */
export const RuntimeMetadataBundleSchema = z.object({
  model: ModelDefinitionSchema,
  views: z.array(ViewDefinitionSchema),
  workflow: WorkflowDefinitionSchema.optional(),
  actions: z.array(ActionDefinitionSchema).optional(),
  permissions: z.array(PermissionDefinitionSchema).optional(),
});
