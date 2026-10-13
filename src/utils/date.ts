import { BiometricAppointment } from '../types';

export const ARABIC_MONTHS = [
  'يناير', 'فبراير', 'مارس', 'أبريل', 'مايو', 'يونيو',
  'يوليو', 'أغسطس', 'سبتمبر', 'أكتوبر', 'نوفمبر', 'ديسمبر'
];

export const ARABIC_WEEKDAYS = [
  'الأحد', 'الإثنين', 'الثلاثاء', 'الأربعاء', 'الخميس', 'الجمعة', 'السبت'
];

export function formatDateToDMY(date: Date): string {
  const day = String(date.getDate()).padStart(2, '0');
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const year = date.getFullYear();
  return `${day}/${month}/${year}`;
}

export function parseDMY(dateStr: string): Date {
  const parts = dateStr.split('/');
  if (parts.length === 3) {
    return new Date(parseInt(parts[2]), parseInt(parts[1]) - 1, parseInt(parts[0]));
  }
  return new Date(dateStr);
}

export function addDays(date: Date, days: number): Date {
  const result = new Date(date);
  result.setDate(result.getDate() + days);
  return result;
}

export function generate28DaySchedule(
  startDateStr: string = '2026-10-12',
  count: number = 8
): BiometricAppointment[] {
  const startDate = new Date(startDateStr);
  const appointments: BiometricAppointment[] = [];

  for (let i = 0; i < count; i++) {
    const apptDate = addDays(startDate, i * 28);
    const day2 = addDays(apptDate, 1);
    const day3 = addDays(apptDate, 2);

    const formattedDate = formatDateToDMY(apptDate);
    const gracePeriodStart = formattedDate;
    const gracePeriodEnd = formatDateToDMY(day3);

    const day = apptDate.getDate();
    const monthIndex = apptDate.getMonth();
    const monthLabel = ARABIC_MONTHS[monthIndex];

    appointments.push({
      id: `appt-${i + 1}`,
      orderNumber: i + 1,
      date: apptDate.toISOString().split('T')[0],
      formattedDate,
      dayLabel: String(day),
      monthLabel,
      gracePeriodStart,
      gracePeriodEnd,
      time: '10:00 صباحاً',
      status: i === 0 ? 'active_upcoming' : 'scheduled',
      daysRemaining: i === 0 ? 4 : 4 + i * 28,
      isCurrentWindow: i === 0,
      currentDayInWindow: 1,
      notes: i === 0 ? 'الموعد النشط القادم المعتمد في نظام Gocbil' : undefined,
    });
  }

  return appointments;
}

export function getArabicDayOfWeek(date: Date): string {
  return ARABIC_WEEKDAYS[date.getDay()];
}
