import { useEffect, useRef, useState } from 'react';

/**
 * Tracks whether form values have changed from the original.
 * Compares current values against initial values using JSON serialization.
 */
export function useDirtyTracking(
  currentValues: Record<string, unknown>,
  initialValues?: Record<string, unknown>
) {
  const isDirty = useRef(false);
  const [dirty, setDirty] = useState(false);
  const initialRef = useRef<string>('');

  useEffect(() => {
    if (initialValues && initialRef.current === '') {
      initialRef.current = JSON.stringify(initialValues);
    }
  }, [initialValues]);

  useEffect(() => {
    if (!initialRef.current) return;
    const current = JSON.stringify(currentValues);
    const changed = current !== initialRef.current;
    isDirty.current = changed;
    setDirty(changed);
  }, [currentValues]);

  const markClean = () => {
    initialRef.current = JSON.stringify(currentValues);
    isDirty.current = false;
    setDirty(false);
  };

  const markDirty = () => {
    isDirty.current = true;
    setDirty(true);
  };

  return { isDirty: dirty, markClean, markDirty };
}
