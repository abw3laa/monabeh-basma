import { AppSettings, BiometricAppointment, BiometricLog } from '../types';
import { generate28DaySchedule } from './date';

const STORAGE_KEY_APPOINTMENTS = 'fingerprint_app_appointments_v1';
const STORAGE_KEY_HISTORY = 'fingerprint_app_history_v1';
const STORAGE_KEY_SETTINGS = 'fingerprint_app_settings_v1';
const STORAGE_KEY_THEME = 'fingerprint_app_theme_v1';

export const DEFAULT_SETTINGS: AppSettings = {
  cadenceDays: 28,
  alarmTime: '10:00',
  ringtone: 'pulse_alarm',
  vibrationEnabled: true,
  snoozeDurationMinutes: 60,
  autoRescheduleOnBoot: true,
  batteryExemptionGranted: true,
  displayOverAppsGranted: true,
  criticalNotificationsGranted: true,
  offlineMode: true,
};

export const INITIAL_HISTORY: BiometricLog[] = [
  {
    id: 'log-1',
    confirmedAt: '2026-09-14 10:15',
    formattedDate: '14/09/2026',
    cycleNumber: 12,
    dayConfirmed: 'اليوم الأول (الأساسي)',
    status: 'ناجحة وفي الموعد',
    note: 'تم إثبات الحضور وتبصيم البوابة الآلية بنجاح.'
  },
  {
    id: 'log-2',
    confirmedAt: '2026-08-17 11:30',
    formattedDate: '17/08/2026',
    cycleNumber: 11,
    dayConfirmed: 'اليوم الأول (الأساسي)',
    status: 'ناجحة وفي الموعد',
    note: 'توثيق دورة شهر أغسطس في دائرة الهجرة.'
  },
  {
    id: 'log-3',
    confirmedAt: '2026-07-21 09:45',
    formattedDate: '21/07/2026',
    cycleNumber: 10,
    dayConfirmed: 'اليوم الثاني (تذكير)',
    status: 'ناجحة وفي الموعد',
    note: 'تم التبصيم خلال فترة السماح اليوم الثاني.'
  },
  {
    id: 'log-4',
    confirmedAt: '2026-06-22 14:10',
    formattedDate: '22/06/2026',
    cycleNumber: 9,
    dayConfirmed: 'اليوم الأول (الأساسي)',
    status: 'ناجحة وفي الموعد',
    note: 'البصمة الدورية المعتمدة بنظام البصمة.'
  }
];

export function loadAppointments(): BiometricAppointment[] {
  try {
    const saved = localStorage.getItem(STORAGE_KEY_APPOINTMENTS);
    if (saved) {
      return JSON.parse(saved);
    }
  } catch (e) {
    console.warn('Failed to load appointments from localStorage', e);
  }
  return generate28DaySchedule('2026-10-12', 8);
}

export function saveAppointments(appointments: BiometricAppointment[]) {
  try {
    localStorage.setItem(STORAGE_KEY_APPOINTMENTS, JSON.stringify(appointments));
  } catch (e) {
    console.warn('Failed to save appointments', e);
  }
}

export function loadHistory(): BiometricLog[] {
  try {
    const saved = localStorage.getItem(STORAGE_KEY_HISTORY);
    if (saved) {
      return JSON.parse(saved);
    }
  } catch (e) {
    console.warn('Failed to load history', e);
  }
  return INITIAL_HISTORY;
}

export function saveHistory(history: BiometricLog[]) {
  try {
    localStorage.setItem(STORAGE_KEY_HISTORY, JSON.stringify(history));
  } catch (e) {
    console.warn('Failed to save history', e);
  }
}

export function loadSettings(): AppSettings {
  try {
    const saved = localStorage.getItem(STORAGE_KEY_SETTINGS);
    if (saved) {
      return { ...DEFAULT_SETTINGS, ...JSON.parse(saved) };
    }
  } catch (e) {
    console.warn('Failed to load settings', e);
  }
  return DEFAULT_SETTINGS;
}

export function saveSettings(settings: AppSettings) {
  try {
    localStorage.setItem(STORAGE_KEY_SETTINGS, JSON.stringify(settings));
  } catch (e) {
    console.warn('Failed to save settings', e);
  }
}

export function loadSavedTheme(): 'light' | 'dark' {
  try {
    const saved = localStorage.getItem(STORAGE_KEY_THEME);
    if (saved === 'dark' || saved === 'light') return saved;
  } catch {
    // fallback
  }
  return 'light'; // matches default screenshot Image 1 & Image 4
}

export function saveSavedTheme(theme: 'light' | 'dark') {
  try {
    localStorage.setItem(STORAGE_KEY_THEME, theme);
  } catch {
    // fallback
  }
}
