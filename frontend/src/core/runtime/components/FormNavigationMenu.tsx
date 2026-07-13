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

export function FormNavigationMenu() {
  const { data: forms } = useAccessibleForms();
  const navigate = useNavigate();
  const location = useLocation();
  const [open, setOpen] = useState(true);

  if (!forms || forms.length === 0) return null;

  // Filter out sub-forms: only show top-level forms (forms that are not
  // referenced as child forms in sys_form_sub_forms). The API returns all
  // accessible forms including sub-forms, which should only appear as tabs
  // inside their parent form, not as separate sidebar items.
  // For now we use a heuristic: sub-forms typically end with "_line" or "_lines"
  // or their formCode contains "line". This can be replaced with a proper
  // backend flag once available.
  const topForms = forms.filter(
    (f) =>
      !f.formCode.endsWith('_line') &&
      !f.formCode.endsWith('_lines') &&
      !f.formCode.includes('_line_') &&
      !f.formCode.includes('_line_item'),
  );

  if (topForms.length === 0) return null;

  return (
    <>
      <ListSubheader
        sx={{ fontWeight: 600, fontSize: 11, lineHeight: '28px', cursor: 'pointer' }}
        onClick={() => setOpen(!open)}
      >
        {open ? '▼' : '▶'} DYNAMIC FORMS
      </ListSubheader>
      <Collapse in={open}>
        <List dense>
          {topForms.map((f) => {
            const path = `/app/runtime?form=${encodeURIComponent(f.formCode)}`;
            return (
              <ListItem key={f.formCode} disablePadding>
                <ListItemButton
                  onClick={() => navigate(path)}
                  selected={
                    location.pathname === '/app/runtime' &&
                    location.search.includes(f.formCode)
                  }
                  sx={{ mx: 1, mb: 0.3, borderRadius: 1 }}
                >
                  <ListItemIcon sx={{ minWidth: 36 }}>
                    <Description fontSize="small" />
                  </ListItemIcon>
                  <ListItemText
                    primary={f.formLabel}
                    primaryTypographyProps={{ fontSize: 14 }}
                  />
                </ListItemButton>
              </ListItem>
            );
          })}
        </List>
      </Collapse>
    </>
  );
}
