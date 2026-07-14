import { useQuery } from '@tanstack/react-query';

import { fetchMenu, type MenuTreeNode } from '../api/runtimeApi';

/**
 * Hook to fetch the hierarchical menu tree for the current user.
 * The menu is fetched once per session (staleTime: Infinity) since
 * it rarely changes and is cached aggressively.
 */
export function useMenuItems() {
  return useQuery<MenuTreeNode[]>({
    queryKey: ['runtime', 'menu'],
    queryFn: fetchMenu,
    staleTime: Infinity,
    gcTime: Infinity,
  });
}
