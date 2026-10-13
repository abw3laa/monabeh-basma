export type ThemeMode = 'light' | 'dark';

export type TabType = 'home' | 'schedule' | 'history' | 'settings' | 'welcome';

export interface BiometricAppointment {
  id: string;
  orderNumber: number;
  date: string; // ISO string or YYYY-MM-DD
  formattedDate: string; // e.g. 12/10/2026
  dayLabel: string;
  monthLabel: string;
  gracePeriodStart: string;
  gracePeriodEnd: string;
  time: string; // e.g. 10:00 صباحاً
  status: 'active_upcoming' | 'scheduled' | 'completed' | 'urgent';
  daysRemaining: number;
  isCurrentWindow: boolean;
  currentDayInWindow: 1 | 2 | 3; // Day 1: الأساسي, Day 2: تذكير, Day 3: الفرصة الأخيرة
  notes?: string;
}

export interface BiometricLog {
  id: string;
  appointmentId?: string;
  confirmedAt: string; // Date time
  formattedDate: string;
  cycleNumber: number;
  dayConfirmed: 'اليوم الأول (الأساسي)' | 'اليوم الثاني (تذكير)' | 'اليوم الثالث (الفرصة الأخيرة)';
  status: 'ناجحة وفي الموعد' | 'مؤكدة يدوياً';
  note: string;
}

export interface AppSettings {
  cadenceDays: number; // 28 by default
  alarmTime: string; // "10:00"
  ringtone: string; // "urgent_beep" | "pulse_alarm" | "official_bell"
  vibrationEnabled: boolean;
  snoozeDurationMinutes: number; // 60 minutes default
  autoRescheduleOnBoot: boolean;
  batteryExemptionGranted: boolean;
  displayOverAppsGranted: boolean;
  criticalNotificationsGranted: boolean;
  offlineMode: boolean;
}
