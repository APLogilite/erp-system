import { Description } from '@mui/icons-material';
import {
  Collapse,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  ListSubheader,
} from '@mui/material';
import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

import { useAccessibleForms } from '../hooks/useAccessibleForms';

/**
 * Legacy sidebar navigation rendered from accessible windows.
 * Replaced by MenuNavigation (PRD-004) in the sidebar, but kept for backward compatibility.
 */
export function FormNavigationMenu() {
  const { data: windows } = useAccessibleForms();
  const navigate = useNavigate();
  const location = useLocation();
  const [open, setOpen] = useState(true);

  if (!windows || windows.length === 0) return null;

  return (
    <>
      <ListSubheader
        sx={{ fontWeight: 600, fontSize: 11, lineHeight: '28px', cursor: 'pointer' }}
        onClick={() => setOpen(!open)}
      >
        {open ? '▼' : '▶'} WINDOWS
      </ListSubheader>
      <Collapse in={open}>
        <List dense>
          {windows.map((w) => {
            const path = `/window/${encodeURIComponent(w.windowName)}`;
            return (
              <ListItem key={w.windowName} disablePadding>
                <ListItemButton
                  onClick={() => navigate(path)}
                  selected={
                    location.pathname === '/app/window' && location.pathname.includes(w.windowName)
                  }
                  sx={{ mx: 1, mb: 0.3, borderRadius: 1 }}
                >
                  <ListItemIcon sx={{ minWidth: 36 }}>
                    <Description fontSize="small" />
                  </ListItemIcon>
                  <ListItemText primary={w.windowLabel} primaryTypographyProps={{ fontSize: 14 }} />
                </ListItemButton>
              </ListItem>
            );
          })}
        </List>
      </Collapse>
    </>
  );
}
