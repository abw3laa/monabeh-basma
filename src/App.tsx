/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect } from 'react';
import { TabType, ThemeMode, BiometricAppointment, BiometricLog, AppSettings } from './types';
import {
  loadAppointments,
  saveAppointments,
  loadHistory,
  saveHistory,
  loadSettings,
  saveSettings,
  loadSavedTheme,
  saveSavedTheme,
} from './utils/storage';
import { generate28DaySchedule, formatDateToDMY } from './utils/date';
import { playHapticSuccess } from './utils/audio';

import { Header } from './components/Header';
import { BottomNav } from './components/BottomNav';
import { HomeScreen } from './components/HomeScreen';
import { ScheduleScreen } from './components/ScheduleScreen';
import { HistoryScreen } from './components/HistoryScreen';
import { SettingsScreen } from './components/SettingsScreen';
import { WelcomeScreen } from './components/WelcomeScreen';

import { AlarmModal } from './components/AlarmModal';
import { InstructionsModal } from './components/InstructionsModal';
import { PermissionsModal } from './components/PermissionsModal';
import { NewAppointmentModal } from './components/NewAppointmentModal';
import { KotlinCodeModal } from './components/KotlinCodeModal';
import { CheckCircle2 } from 'lucide-react';

export default function App() {
  const [theme, setTheme] = useState<ThemeMode>(() => loadSavedTheme());
  const [activeTab, setActiveTab] = useState<TabType>('home');
  const [appointments, setAppointments] = useState<BiometricAppointment[]>(() => loadAppointments());
  const [historyLogs, setHistoryLogs] = useState<BiometricLog[]>(() => loadHistory());
  const [settings, setSettings] = useState<AppSettings>(() => loadSettings());

  // Modals state
  const [showAlarmModal, setShowAlarmModal] = useState(false);
  const [showInstructionsModal, setShowInstructionsModal] = useState(false);
  const [showPermissionsModal, setShowPermissionsModal] = useState(false);
  const [showNewAppointmentModal, setShowNewAppointmentModal] = useState(false);
  const [showKotlinModal, setShowKotlinModal] = useState(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  useEffect(() => {
    saveSavedTheme(theme);
    if (theme === 'dark') {
      document.documentElement.classList.add('dark');
      document.body.classList.add('bg-[#000f1d]', 'text-white');
      document.body.classList.remove('bg-[#f7f9fb]', 'text-[#191c1e]');
    } else {
      document.documentElement.classList.remove('dark');
      document.body.classList.add('bg-[#f7f9fb]', 'text-[#191c1e]');
      document.body.classList.remove('bg-[#000f1d]', 'text-white');
    }
  }, [theme]);

  const toggleTheme = () => {
    setTheme((prev) => (prev === 'light' ? 'dark' : 'light'));
  };

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => {
      setToastMessage(null);
    }, 3500);
  };

  const handleUpdateSettings = (newSettings: Partial<AppSettings>) => {
    const updated = { ...settings, ...newSettings };
    setSettings(updated);
    saveSettings(updated);
    showToast('تم حفظ الإعدادات بنجاح');
  };

  const handleConfirmBiometric = () => {
    playHapticSuccess();
    const activeAppt = appointments[0];
    const today = new Date();
    const formattedToday = formatDateToDMY(today);

    // Create log
    const newLog: BiometricLog = {
      id: `log-${Date.now()}`,
      appointmentId: activeAppt?.id,
      confirmedAt: today.toISOString(),
      formattedDate: activeAppt?.formattedDate || formattedToday,
      cycleNumber: historyLogs.length + 1,
      dayConfirmed: 'اليوم الأول (الأساسي)',
      status: 'ناجحة وفي الموعد',
      note: 'تم تأكيد الحضور وتبصيم البوابة الآلية بنجاح ومطابقة قيود النظام.',
    };

    const updatedHistory = [newLog, ...historyLogs];
    setHistoryLogs(updatedHistory);
    saveHistory(updatedHistory);

    // If we have appointments, rotate or update the active one
    showToast('تمت البصمة بنجاح! تم توثيق الموعد في السجل وجدولة الدورة القادمة.');
  };

  const handleSaveNewSchedule = (startDateStr: string, cadenceDays: number) => {
    const newSchedule = generate28DaySchedule(startDateStr, 8);
    setAppointments(newSchedule);
    saveAppointments(newSchedule);
    handleUpdateSettings({ cadenceDays });
    showToast('تم تحديث جدول البصمات وفق الدورة الجديدة');
  };

  const handleResetData = () => {
    if (window.confirm('هل أنت متأكد من إعادة تعيين كافة البيانات إلى الإعدادات الافتراضية؟')) {
      localStorage.clear();
      const freshSchedule = generate28DaySchedule('2026-10-12', 8);
      setAppointments(freshSchedule);
      saveAppointments(freshSchedule);
      setTheme('light');
      showToast('تمت استعادة البيانات الافتراضية بنجاح');
    }
  };

  const activeAppointment = appointments[0] || {
    id: 'appt-1',
    orderNumber: 1,
    date: '2026-10-12',
    formattedDate: '12/10/2026',
    dayLabel: '12',
    monthLabel: 'أكتوبر',
    gracePeriodStart: '12/10/2026',
    gracePeriodEnd: '14/10/2026',
    time: '10:00 صباحاً',
    status: 'active_upcoming',
    daysRemaining: 4,
    isCurrentWindow: true,
    currentDayInWindow: 1,
  };

  return (
    <div className={`min-h-screen flex flex-col font-sans transition-colors ${
      theme === 'dark' ? 'bg-[#000f1d] text-white' : 'bg-[#f7f9fb] text-[#191c1e]'
    }`}>
      {/* Toast Notification */}
      {toastMessage && (
        <div className="fixed top-14 left-1/2 -translate-x-1/2 z-50 px-4 py-2.5 rounded-2xl bg-teal-800 text-white shadow-xl flex items-center gap-2 text-xs font-bold animate-in fade-in slide-in-from-top-4 duration-200">
          <CheckCircle2 className="w-4 h-4 text-teal-300" />
          <span>{toastMessage}</span>
        </div>
      )}

      {/* Main Top Header */}
      <Header
        theme={theme}
        onToggleTheme={toggleTheme}
        onTriggerAlarm={() => setShowAlarmModal(true)}
        onOpenKotlinCode={() => setShowKotlinModal(true)}
      />

      {/* Screen Tabs Content */}
      <main className="flex-1 flex flex-col">
        {activeTab === 'welcome' && (
          <WelcomeScreen
            theme={theme}
            onStart={() => setActiveTab('home')}
          />
        )}

        {activeTab === 'home' && (
          <HomeScreen
            activeAppointment={activeAppointment}
            onConfirmBiometric={handleConfirmBiometric}
            onOpenNewAppointment={() => setShowNewAppointmentModal(true)}
            onOpenInstructions={() => setShowInstructionsModal(true)}
            onOpenPermissions={() => setShowPermissionsModal(true)}
            theme={theme}
          />
        )}

        {activeTab === 'schedule' && (
          <ScheduleScreen
            appointments={appointments}
            historyLogs={historyLogs}
            onOpenNewAppointment={() => setShowNewAppointmentModal(true)}
            onOpenPermissions={() => setShowPermissionsModal(true)}
            theme={theme}
          />
        )}

        {activeTab === 'history' && (
          <HistoryScreen
            historyLogs={historyLogs}
            theme={theme}
            onOpenPermissions={() => setShowPermissionsModal(true)}
          />
        )}

        {activeTab === 'settings' && (
          <SettingsScreen
            settings={settings}
            onUpdateSettings={handleUpdateSettings}
            onOpenWelcome={() => setActiveTab('welcome')}
            onOpenKotlinCode={() => setShowKotlinModal(true)}
            onToggleTheme={toggleTheme}
            theme={theme}
          />
        )}
      </main>

      {/* Bottom Navigation Dock */}
      {activeTab !== 'welcome' && (
        <BottomNav
          activeTab={activeTab}
          onChangeTab={setActiveTab}
          theme={theme}
        />
      )}

      {/* Fullscreen Active Alarm Ringing Simulation Modal */}
      <AlarmModal
        isOpen={showAlarmModal}
        onClose={() => setShowAlarmModal(false)}
        onConfirmBiometric={handleConfirmBiometric}
        onSnooze={() => {
          showToast('تمت جدولة الغفوة • سيعاد التنبيه تلقائياً بعد 60 دقيقة');
        }}
        theme={theme}
        dateStr={`${activeAppointment.dayLabel} ${activeAppointment.monthLabel} 2026`}
        timeStr={activeAppointment.time}
      />

      {/* Instructions Modal (Image 16 & 20) */}
      <InstructionsModal
        isOpen={showInstructionsModal}
        onClose={() => setShowInstructionsModal(false)}
        theme={theme}
      />

      {/* Permissions Modal (Image 12 & 18) */}
      <PermissionsModal
        isOpen={showPermissionsModal}
        onClose={() => setShowPermissionsModal(false)}
        settings={settings}
        onUpdateSettings={handleUpdateSettings}
        theme={theme}
      />

      {/* New Appointment Modal */}
      <NewAppointmentModal
        isOpen={showNewAppointmentModal}
        onClose={() => setShowNewAppointmentModal(false)}
        onSaveSchedule={handleSaveNewSchedule}
        onOpenInstructions={() => setShowInstructionsModal(true)}
        theme={theme}
      />

      {/* Kotlin Android Project Code Hub Modal */}
      <KotlinCodeModal
        isOpen={showKotlinModal}
        onClose={() => setShowKotlinModal(false)}
        theme={theme}
      />
    </div>
  );
}
