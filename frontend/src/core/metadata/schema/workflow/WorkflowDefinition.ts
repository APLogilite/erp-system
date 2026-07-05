import { BaseMetadata } from '../BaseMetadata';
import { ExpressionDefinition } from '../expression/ExpressionDefinition';

export interface WorkflowState {
  /** Unique state identifier within the workflow, e.g. 'draft', 'active' */
  code: string;
  /** Human‑readable label for the state */
  name: string;
  /** Flag identifying initial state */
  initial?: boolean;
  /** Flag identifying terminal/final state */
  final?: boolean;
}

export interface WorkflowTransition {
  /** Unique transition code, e.g., 'submit', 'approve' */
  code: string;
  /** Human-readable transition label */
  label?: string;
  /** Source state code */
  fromState: string;
  /** Target state code */
  toState: string;
  /** Condition expression to allow transition */
  guardExpression?: ExpressionDefinition;
  /** Action codes to invoke on transition */
  actions?: string[];
  /** Required permissions to trigger the transition */
  permissions?: string[];
}

export interface WorkflowDefinition extends BaseMetadata {
  /** The model code this workflow belongs to */
  modelCode: string;
  /** List of all states in the lifecycle */
  states: WorkflowState[];
  /** List of valid state transitions */
  transitions: WorkflowTransition[];
}
