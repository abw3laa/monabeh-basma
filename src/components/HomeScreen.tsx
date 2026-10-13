import React from 'react';
import { BiometricAppointment, ThemeMode } from '../types';
import { ShieldCheck, Info, Plus, Clock, Hourglass, Calendar, Fingerprint, Home } from 'lucide-react';

interface HomeScreenProps {
  activeAppointment: BiometricAppointment;
  onConfirmBiometric: () => void;
  onOpenNewAppointment: () => void;
  onOpenInstructions: () => void;
  onOpenPermissions: () => void;
  theme: ThemeMode;
}

export const HomeScreen: React.FC<HomeScreenProps> = ({
  activeAppointment,
  onConfirmBiometric,
  onOpenNewAppointment,
  onOpenInstructions,
  onOpenPermissions,
  theme,
}) => {
  const isDark = theme === 'dark';

  return (
    <div className={`w-full min-h-[calc(100vh-8rem)] px-4 pb-24 pt-2 transition-colors ${
      isDark ? 'bg-[#000f1d] text-white' : 'bg-[#f7f9fb] text-[#191c1e]'
    }`}>
      <div className="max-w-md mx-auto w-full space-y-4">
        {/* Screen Title Bar */}
        <div className="flex items-center justify-between pt-1">
          {/* Right: Title & Icon */}
          <div className="flex items-center gap-2">
            <Home className="w-5 h-5 text-[#006a61] dark:text-teal-400" />
            <h1 className="text-xl font-bold tracking-tight">الرئيسية</h1>
          </div>

          {/* Left: Fingerprint Avatar */}
          <button
            onClick={onOpenPermissions}
            className={`w-10 h-10 rounded-full flex items-center justify-center border transition-all cursor-pointer ${
              isDark
                ? 'bg-[#0f2537] border-slate-700 text-teal-300 hover:bg-[#16334d]'
                : 'bg-teal-50 border-teal-200 text-[#006a61] hover:bg-teal-100'
            }`}
            title="الأذونات والإشعارات"
          >
            <span className="material-symbols-outlined text-[20px]">fingerprint</span>
          </button>
        </div>

        {/* Card 1: Active & Secured Alarm Banner */}
        <div
          className={`w-full rounded-2xl p-4 border flex items-center justify-between gap-3 shadow-sm transition-all ${
            isDark
              ? 'bg-[#071928] border-slate-800'
              : 'bg-white border-slate-200'
          }`}
        >
          {/* Left: Info button */}
          <button
            onClick={onOpenInstructions}
            className="p-1 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 transition-colors cursor-pointer"
            title="تعليمات الإعداد"
          >
            <Info className="w-5 h-5" />
          </button>

          {/* Middle: Text details */}
          <div className="text-right flex-1">
            <div className="flex items-center justify-end gap-1.5 font-bold text-sm">
              <span className="text-emerald-500 text-xs">✔</span>
              <span>المنبه الذكي نشط ومؤمّن</span>
            </div>
            <p className={`text-xs mt-0.5 ${isDark ? 'text-slate-400' : 'text-slate-500'}`}>
              تم ضبط تنبيهات مهلة الأيام الثلاثة بدقة
            </p>
          </div>

          {/* Right: Shield Icon */}
          <button
            onClick={onOpenPermissions}
            className={`w-10 h-10 rounded-xl flex items-center justify-center shrink-0 cursor-pointer ${
              isDark ? 'bg-teal-950/70 text-teal-400 border border-teal-800' : 'bg-teal-50 text-[#006a61] border border-teal-200'
            }`}
            title="فحص أمان المنبه والأذونات"
          >
            <ShieldCheck className="w-5 h-5" />
          </button>
        </div>

        {/* Card 2: Add New Fingerprint Button */}
        <button
          onClick={onOpenNewAppointment}
          className={`w-full py-3.5 px-4 rounded-2xl border shadow-sm flex items-center justify-center gap-2 font-bold text-sm transition-all active:scale-98 cursor-pointer ${
            isDark
              ? 'bg-[#071928] border-slate-800 text-teal-300 hover:bg-[#0c2336]'
              : 'bg-white border-slate-200 text-[#006a61] hover:bg-slate-50'
          }`}
        >
          <div className="w-6 h-6 rounded-full bg-teal-100 dark:bg-teal-950 flex items-center justify-center">
            <Plus className="w-4 h-4 text-[#006a61] dark:text-teal-400" />
          </div>
          <span>تسجيل بصمة جديدة</span>
        </button>

        {/* Card 3: Upcoming Appointment Hero Card */}
        <div
          className={`w-full rounded-2xl p-5 border shadow-sm transition-all ${
            isDark
              ? 'bg-[#071928] border-slate-800 text-white'
              : 'bg-white border-slate-200 text-slate-900'
          }`}
        >
          {/* Top Pill Badges */}
          <div className="flex items-center justify-between mb-4">
            {/* Time pill */}
            <div className={`flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold ${
              isDark ? 'bg-slate-800/80 text-slate-300' : 'bg-slate-100 text-slate-700'
            }`}>
              <Clock className="w-3.5 h-3.5 text-teal-500" />
              <span>{activeAppointment.time || '10:00 صباحاً'}</span>
            </div>

            {/* Status pill: Soon */}
            <div className="flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold bg-teal-100 dark:bg-teal-950/80 text-[#006a61] dark:text-teal-300">
              <span className="w-2 h-2 rounded-full bg-teal-500 animate-pulse" />
              <span>قريباً</span>
            </div>
          </div>

          {/* Subtitle & Big Date Display */}
          <div className="text-right space-y-1 mb-5">
            <span className={`text-xs font-medium block ${isDark ? 'text-slate-400' : 'text-slate-500'}`}>
              موعد البصمة التالي المقرر
            </span>

            <div className="flex items-center justify-between pt-1">
              {/* Countdown badge: Days remaining */}
              <div className={`flex items-center gap-1 px-3 py-1 rounded-full text-xs font-bold ${
                isDark ? 'bg-teal-950/60 text-teal-300 border border-teal-800' : 'bg-teal-50 text-teal-800 border border-teal-200'
              }`}>
                <Hourglass className="w-3.5 h-3.5" />
                <span>متبقي {activeAppointment.daysRemaining || 4} أيام</span>
              </div>

              {/* Big Date */}
              <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight font-mono text-left rtl:text-right">
                {activeAppointment.formattedDate}
              </h2>
            </div>
          </div>

          {/* Permitted Days Window Header */}
          <div className="flex items-center justify-between mb-3 text-xs">
            <span className={`font-mono ${isDark ? 'text-slate-400' : 'text-slate-500'}`}>
              {activeAppointment.gracePeriodEnd} - {activeAppointment.gracePeriodStart}
            </span>
            <div className="flex items-center gap-1 font-bold text-teal-700 dark:text-teal-300">
              <span>الأيام المسموحة</span>
              <Calendar className="w-4 h-4" />
            </div>
          </div>

          {/* 3-Day Window Cards Grid (Exact matching of Day 1, Day 2, Day 3) */}
          <div className="grid grid-cols-3 gap-2.5 mb-5 text-center">
            {/* Day 1: Primary (الأساسي) */}
            <div
              className={`rounded-xl p-3 flex flex-col items-center justify-between border transition-all ${
                isDark
                  ? 'bg-[#0f2537] border-teal-700/50 text-teal-300'
                  : 'bg-[#eef8f6] border-teal-300/80 text-teal-900'
              }`}
            >
              <span className="text-[11px] font-semibold opacity-80">اليوم 1</span>
              <span className="text-2xl font-black my-1 font-mono">{activeAppointment.dayLabel}</span>
              <span className="text-[10px] font-medium opacity-80 mb-2">{activeAppointment.monthLabel}</span>
              <span className="w-full py-1 rounded-md text-[10px] font-bold bg-[#006a61] text-white">
                الأساسي
              </span>
            </div>

            {/* Day 2: Reminder (تذكير) */}
            <div
              className={`rounded-xl p-3 flex flex-col items-center justify-between border transition-all ${
                isDark
                  ? 'bg-[#091b2c] border-slate-800 text-slate-300'
                  : 'bg-slate-50 border-slate-200 text-slate-700'
              }`}
            >
              <span className="text-[11px] font-semibold opacity-75">اليوم 2</span>
              <span className="text-2xl font-black my-1 font-mono">
                {parseInt(activeAppointment.dayLabel) + 1}
              </span>
              <span className="text-[10px] font-medium opacity-75 mb-2">{activeAppointment.monthLabel}</span>
              <span className={`w-full py-1 rounded-md text-[10px] font-bold ${
                isDark ? 'bg-slate-800 text-slate-300' : 'bg-slate-200 text-slate-600'
              }`}>
                تذكير
              </span>
            </div>

            {/* Day 3: Last Chance (الفرصة الأخيرة) */}
            <div
              className={`rounded-xl p-3 flex flex-col items-center justify-between border transition-all ${
                isDark
                  ? 'bg-rose-950/30 border-rose-900/60 text-rose-300'
                  : 'bg-rose-50/70 border-rose-200 text-rose-900'
              }`}
            >
              <span className="text-[11px] font-semibold opacity-85">اليوم 3</span>
              <span className="text-2xl font-black my-1 font-mono text-red-600 dark:text-red-400">
                {parseInt(activeAppointment.dayLabel) + 2}
              </span>
              <span className="text-[10px] font-medium opacity-85 mb-2">{activeAppointment.monthLabel}</span>
              <span className="w-full py-1 rounded-md text-[10px] font-bold bg-red-600 text-white">
                الفرصة الأخيرة
              </span>
            </div>
          </div>

          {/* Confirm Biometric Action Button */}
          <button
            onClick={onConfirmBiometric}
            className="w-full h-13 rounded-xl bg-[#006a61] hover:bg-[#00574f] text-white font-bold text-base flex items-center justify-center gap-2 shadow-md shadow-teal-900/10 active:scale-98 transition-all cursor-pointer"
          >
            <Fingerprint className="w-5 h-5" />
            <span>تأكيد البصمة</span>
          </button>
        </div>
      </div>
    </div>
  );
};
