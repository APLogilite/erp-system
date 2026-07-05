import { create } from 'zustand';

interface GridState {
  sortBy: string | null;
  filter: Record<string, unknown>;
  page: number;
  pageSize: number;
  selection: Array<string | number>;
  setSortBy: (field: string | null) => void;
  setFilter: (filter: Record<string, unknown>) => void;
  setPage: (page: number) => void;
  setPageSize: (size: number) => void;
  setSelection: (selection: Array<string | number>) => void;
}

export const useGridState = create<GridState>((set) => ({
  sortBy: null,
  filter: {},
  page: 1,
  pageSize: 20,
  selection: [],
  setSortBy: (field: string | null) => set(() => ({ sortBy: field })),
  setFilter: (filter: Record<string, unknown>) => set(() => ({ filter })),
  setPage: (page: number) => set(() => ({ page })),
  setPageSize: (size: number) => set(() => ({ pageSize: size })),
  setSelection: (selection: Array<string | number>) => set(() => ({ selection })),
}));
