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

  // Group forms by model
  const grouped = new Map<string, typeof forms>();
  for (const f of forms) {
    const model = f.modelLabel || f.modelName;
    if (!grouped.has(model)) grouped.set(model, []);
    grouped.get(model)!.push(f);
  }

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
          {Array.from(grouped.entries()).map(([model, modelForms]) => (
            <div key={model}>
              {grouped.size > 1 && (
                <ListSubheader sx={{ fontSize: 10, lineHeight: '20px', color: 'text.secondary' }}>
                  {model}
                </ListSubheader>
              )}
              {modelForms.map((f) => {
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
            </div>
          ))}
        </List>
      </Collapse>
    </>
  );
}
