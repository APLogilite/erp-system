/**
 * useDefinitionGeneration (ENH-004)
 *
 * Polls the backend data-generation marker every 30 seconds (and on window
 * focus). When the marker changes — i.e. the DB was reseeded or migrated —
 * all cached window definitions/records are invalidated automatically, so
 * open tabs self-heal instead of rendering ghost UUIDs from a previous
 * database generation.
 *
 * The last-seen marker is persisted in localStorage so a freshly opened tab
 * also detects a generation change on first load.
 */

import { useEffect, useRef } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';

import { fetchDefinitionGeneration } from '../api/runtimeApi';

const STORAGE_KEY = 'erp-definition-generation';
const POLL_INTERVAL_MS = 30_000;

/** Query keys whose cached data embeds generation-specific UUIDs. */
const GENERATION_BOUND_QUERY_KEYS = [
  'window-definition',
  'window-records',
  'window-record',
  'runtime-menu',
  'dynamic-lookup',
];

export function useDefinitionGeneration(): void {
  const queryClient = useQueryClient();

  // null until the first successful fetch of this JS context seeds it
  const lastSeenRef = useRef<string | null>(localStorage.getItem(STORAGE_KEY));

  const { data: generation } = useQuery({
    queryKey: ['definition-generation'],
    queryFn: fetchDefinitionGeneration,
    staleTime: 0,
    refetchInterval: POLL_INTERVAL_MS,
    refetchOnWindowFocus: true,
    retry: 1,
    // Never surface errors to the user — a failed poll just skips this round
    throwOnError: false,
  });

  useEffect(() => {
    if (!generation) return;

    const lastSeen = lastSeenRef.current;

    // Persist the new marker BEFORE invalidating to prevent invalidate loops
    lastSeenRef.current = generation;
    localStorage.setItem(STORAGE_KEY, generation);

    if (lastSeen === null || lastSeen === generation) {
      return; // first fetch of this session, or stable generation
    }

    // Generation changed (reseed/migration) — drop all cached data that
    // embeds UUIDs from the old generation
    for (const key of GENERATION_BOUND_QUERY_KEYS) {
      queryClient.invalidateQueries({ queryKey: [key] });
    }
  }, [generation, queryClient]);
}
