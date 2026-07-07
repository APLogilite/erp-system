import { DarkMode, Language, Save } from '@mui/icons-material';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  TextField,
  MenuItem,
  Button,
  Alert,
} from '@mui/material';
import { useState } from 'react';

import { selectCurrentTheme } from '@/core/store/ui/uiSelectors';
import { useUiStore } from '@/core/store/ui/uiStore';

const languages = [
  { value: 'en', label: 'English' },
  { value: 'es', label: 'Spanish' },
  { value: 'fr', label: 'French' },
  { value: 'de', label: 'German' },
  { value: 'zh', label: 'Chinese' },
  { value: 'ja', label: 'Japanese' },
];

const timezones = [
  { value: 'UTC', label: 'UTC' },
  { value: 'America/New_York', label: 'Eastern (UTC-5)' },
  { value: 'America/Chicago', label: 'Central (UTC-6)' },
  { value: 'America/Denver', label: 'Mountain (UTC-7)' },
  { value: 'America/Los_Angeles', label: 'Pacific (UTC-8)' },
  { value: 'Europe/London', label: 'London (UTC+0)' },
  { value: 'Europe/Berlin', label: 'Berlin (UTC+1)' },
  { value: 'Asia/Tokyo', label: 'Tokyo (UTC+9)' },
  { value: 'Asia/Shanghai', label: 'Shanghai (UTC+8)' },
];

const dateFormats = [
  { value: 'MM/DD/YYYY', label: 'MM/DD/YYYY' },
  { value: 'DD/MM/YYYY', label: 'DD/MM/YYYY' },
  { value: 'YYYY-MM-DD', label: 'YYYY-MM-DD' },
];

const numberFormats = [
  { value: '1,234.56', label: '1,234.56' },
  { value: '1.234,56', label: '1.234,56' },
];

export function PreferencesPage() {
  const theme = useUiStore(selectCurrentTheme);
  const setTheme = useUiStore((s) => s.setTheme);

  const [language, setLanguage] = useState('en');
  const [timezone, setTimezone] = useState('UTC');
  const [dateFormat, setDateFormat] = useState('MM/DD/YYYY');
  const [numberFormat, setNumberFormat] = useState('1,234.56');
  const [saved, setSaved] = useState(false);

  const handleSave = () => {
    setSaved(true);
    setTimeout(() => setSaved(false), 2000);
  };

  return (
    <Box sx={{ maxWidth: 700, mx: 'auto', p: 3 }}>
      <Typography variant="h5" fontWeight={700} gutterBottom>
        User Preferences
      </Typography>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
        Customize your workspace experience.
      </Typography>

      <Card sx={{ borderRadius: 3, mb: 3 }}>
        <CardContent sx={{ p: 3 }}>
          <Typography variant="subtitle1" fontWeight={600} gutterBottom>
            <DarkMode sx={{ mr: 1, verticalAlign: 'middle' }} fontSize="small" />
            Appearance
          </Typography>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField
                select
                fullWidth
                label="Theme"
                value={theme}
                onChange={(e) => setTheme(e.target.value as 'light' | 'dark')}
              >
                <MenuItem value="light">Light</MenuItem>
                <MenuItem value="dark">Dark</MenuItem>
              </TextField>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      <Card sx={{ borderRadius: 3, mb: 3 }}>
        <CardContent sx={{ p: 3 }}>
          <Typography variant="subtitle1" fontWeight={600} gutterBottom>
            <Language sx={{ mr: 1, verticalAlign: 'middle' }} fontSize="small" />
            Regional
          </Typography>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField
                select
                fullWidth
                label="Language"
                value={language}
                onChange={(e) => setLanguage(e.target.value)}
              >
                {languages.map((l) => (
                  <MenuItem key={l.value} value={l.value}>
                    {l.label}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                select
                fullWidth
                label="Timezone"
                value={timezone}
                onChange={(e) => setTimezone(e.target.value)}
              >
                {timezones.map((tz) => (
                  <MenuItem key={tz.value} value={tz.value}>
                    {tz.label}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                select
                fullWidth
                label="Date Format"
                value={dateFormat}
                onChange={(e) => setDateFormat(e.target.value)}
              >
                {dateFormats.map((df) => (
                  <MenuItem key={df.value} value={df.value}>
                    {df.label}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                select
                fullWidth
                label="Number Format"
                value={numberFormat}
                onChange={(e) => setNumberFormat(e.target.value)}
              >
                {numberFormats.map((nf) => (
                  <MenuItem key={nf.value} value={nf.value}>
                    {nf.label}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {saved && (
        <Alert severity="success" sx={{ mb: 2 }}>
          Preferences saved.
        </Alert>
      )}
      <Button
        variant="contained"
        startIcon={<Save />}
        onClick={handleSave}
        sx={{ borderRadius: 2, textTransform: 'none' }}
      >
        Save Preferences
      </Button>
    </Box>
  );
}
