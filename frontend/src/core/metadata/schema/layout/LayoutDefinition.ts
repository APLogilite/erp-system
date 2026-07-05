export enum LayoutType {
  PAGE = 'PAGE',
  SECTION = 'SECTION',
  GROUP = 'GROUP',
  ROW = 'ROW',
  COLUMN = 'COLUMN',
  TABS = 'TABS',
  TAB = 'TAB',
  GRID = 'GRID',
  PANEL = 'PANEL',
}

export interface LayoutDefinition {
  /** The component layout type */
  type: LayoutType;
  /** Nested children layout blocks */
  children?: LayoutDefinition[];
  /** Flexible configuration parameters (e.g. grid spacing, column spans) */
  config?: Record<string, unknown>;
}
