import { create } from 'zustand';

interface FormState {
  values: Record<string, unknown>;
  errors: Record<string, string>;
  touched: Record<string, boolean>;
  setValue: (field: string, value: unknown) => void;
  setError: (field: string, message: string) => void;
  setTouched: (field: string, touched: boolean) => void;
}

export const useFormState = create<FormState>((set) => ({
  values: {},
  errors: {},
  touched: {},
  setValue: (field: string, value: unknown) =>
    set((state: FormState) => ({ values: { ...state.values, [field]: value } })),
  setError: (field: string, message: string) =>
    set((state: FormState) => ({ errors: { ...state.errors, [field]: message } })),
  setTouched: (field: string, touched: boolean) =>
    set((state: FormState) => ({ touched: { ...state.touched, [field]: touched } })),
}));
