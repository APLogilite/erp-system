import { BaseMetadata } from '../BaseMetadata';

/** Supported relation types */
export enum RelationType {
  MANY_TO_ONE = 'MANY_TO_ONE',
  ONE_TO_MANY = 'ONE_TO_MANY',
  MANY_TO_MANY = 'MANY_TO_MANY',
  TREE = 'TREE',
}

/** Loading strategies for related data */
export enum LoadingStrategy {
  LAZY = 'LAZY',
  EAGER = 'EAGER',
  PAGINATED = 'PAGINATED',
}

export interface RelationDefinition extends BaseMetadata {
  /** Type of relation */
  relationType: RelationType;
  /** Target model code that this relation points to */
  targetModel: string;
  /** Field in the target model used for display (e.g., name) */
  displayField: string;
  /** Field in the target model that stores the identifier */
  valueField: string;
  /** Whether saving the parent also saves the related entity */
  cascadeSave: boolean;
  /** Loading strategy for fetching the relation */
  loadingStrategy: LoadingStrategy;
}
