import React, { useState } from 'react';
import { BiometricAppointment, BiometricLog, ThemeMode } from '../types';
import { Calendar, Plus, Hourglass, History, CheckCircle2 } from 'lucide-react';

interface ScheduleScreenProps {
  appointments: BiometricAppointment[];
  historyLogs: BiometricLog[];
  onOpenNewAppointment: () => void;
  onOpenPermissions: () => void;
  theme: ThemeMode;
}

export const ScheduleScreen: React.FC<ScheduleScreenProps> = ({
  appointments,
  historyLogs,
  onOpenNewAppointment,
  onOpenPermissions,
  theme,
}) => {
  const [subTab, setSubTab] = useState<'upcoming' | 'past'>('upcoming');
  const isDark = theme === 'dark';

  return (
    <div className={`w-full min-h-[calc(100vh-8rem)] px-4 pb-24 pt-2 transition-colors ${
      isDark ? 'bg-[#000f1d] text-white' : 'bg-[#f7f9fb] text-[#191c1e]'
    }`}>
      <div className="max-w-md mx-auto w-full space-y-4">
        {/* Screen Header Bar */}
        <div className="flex items-center justify-between pt-1">
          <div className="flex items-center gap-2">
            <Calendar className="w-5 h-5 text-[#006a61] dark:text-teal-400" />
            <h1 className="text-xl font-bold tracking-tight">سجل المواعيد</h1>
          </div>

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

        {/* Header Title with Subtitle and New Appointment Button */}
        <div className="flex items-center justify-between gap-2">
          {/* Add New Button */}
          <button
            onClick={onOpenNewAppointment}
            className={`px-3.5 py-2 rounded-xl font-bold text-xs flex items-center gap-1.5 shadow-sm active:scale-95 transition-all cursor-pointer ${
              isDark
                ? 'bg-teal-500 hover:bg-teal-400 text-slate-950'
                : 'bg-[#000f1d] hover:bg-[#0f2537] text-white'
            }`}
          >
            <Plus className="w-4 h-4" />
            <span>بصمة جديدة</span>
          </button>

          {/* Subtitle */}
          <div className="text-right">
            <div className="flex items-center justify-end gap-1.5 font-bold text-base">
              <span>سجل مواعيد البصمة</span>
              <span className="w-2 h-2 rounded-full bg-teal-500" />
            </div>
            <p className={`text-[11px] mt-0.5 ${isDark ? 'text-slate-400' : 'text-slate-500'}`}>
              متابعة تواريخ البصمات الدورية السابقة والمقبلة
            </p>
          </div>
        </div>

        {/* Segmented Tabs: Upcoming vs Past */}
        <div className={`p-1 rounded-2xl flex items-center gap-1 border ${
          isDark ? 'bg-[#071928] border-slate-800' : 'bg-slate-200/60 border-slate-200'
        }`}>
          <button
            onClick={() => setSubTab('upcoming')}
            className={`flex-1 py-2 rounded-xl text-xs font-bold flex items-center justify-center gap-1.5 transition-all cursor-pointer ${
              subTab === 'upcoming'
                ? isDark
                  ? 'bg-[#0f2537] text-teal-300 shadow-sm'
                  : 'bg-white text-[#006a61] shadow-sm'
                : 'text-slate-500 hover:text-slate-800 dark:hover:text-slate-300'
            }`}
          >
            <Calendar className="w-3.5 h-3.5" />
            <span>البصمات القادمة</span>
            <span className="px-1.5 py-0.2 rounded-full text-[10px] bg-teal-100 dark:bg-teal-950 text-teal-800 dark:text-teal-300">
              {appointments.length}
            </span>
          </button>

          <button
            onClick={() => setSubTab('past')}
            className={`flex-1 py-2 rounded-xl text-xs font-bold flex items-center justify-center gap-1.5 transition-all cursor-pointer ${
              subTab === 'past'
                ? isDark
                  ? 'bg-[#0f2537] text-teal-300 shadow-sm'
                  : 'bg-white text-[#006a61] shadow-sm'
                : 'text-slate-500 hover:text-slate-800 dark:hover:text-slate-300'
            }`}
          >
            <History className="w-3.5 h-3.5" />
            <span>البصمات السابقة</span>
            <span className="px-1.5 py-0.2 rounded-full text-[10px] bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300">
              {historyLogs.length}
            </span>
          </button>
        </div>

        {/* Content of Subtabs */}
        {subTab === 'upcoming' ? (
          <div className="space-y-3">
            <div className="text-right text-xs font-bold text-slate-500 dark:text-slate-400">
              قائمة البصمات القادمة
            </div>

            {appointments.map((appt, idx) => {
              const isFirst = idx === 0;

              return (
                <div
                  key={appt.id}
                  className={`w-full rounded-2xl p-4 border shadow-sm transition-all text-right ${
                    isDark
                      ? isFirst
                        ? 'bg-[#071928] border-teal-600/40'
                        : 'bg-[#071928] border-slate-800/80'
                      : isFirst
                        ? 'bg-white border-teal-300/80 shadow-teal-500/5'
                        : 'bg-white border-slate-200'
                  }`}
                >
                  {/* Top Header */}
                  <div className="flex items-center justify-between mb-3">
                    {/* Status Pill */}
                    {isFirst ? (
                      <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-teal-100 dark:bg-teal-950/80 text-[#006a61] dark:text-teal-300">
                        قريباً
                      </span>
                    ) : (
                      <span className={`px-2.5 py-0.5 rounded-full text-xs font-semibold ${
                        isDark ? 'bg-slate-800 text-slate-300' : 'bg-slate-100 text-slate-600'
                      }`}>
                        مجدول
                      </span>
                    )}

                    {/* Order Number & Label */}
                    <div className="flex items-center gap-2">
                      <div className="text-right">
                        <div className="flex items-center gap-1.5 text-xs font-bold">
                          <span>
                            {isFirst
                              ? 'الموعد النشط القادم'
                              : idx === 1
                              ? 'الموعد الثاني المجدول'
                              : idx === 2
                              ? 'الموعد الثالث المجدول'
                              : idx === 3
                              ? 'الموعد الرابع المجدول'
                              : idx === 4
                              ? 'الموعد الخامس المجدول'
                              : `الموعد ${appt.orderNumber} المجدول`}
                          </span>
                          {isFirst && <span className="w-2 h-2 rounded-full bg-teal-500" />}
                        </div>
                      </div>
                      <div
                        className={`w-7 h-7 rounded-lg flex items-center justify-center font-bold text-xs ${
                          isFirst
                            ? 'bg-teal-200 dark:bg-teal-900 text-teal-950 dark:text-teal-200'
                            : isDark
                            ? 'bg-slate-800 text-slate-300'
                            : 'bg-slate-100 text-slate-700'
                        }`}
                      >
                        {appt.orderNumber}
                      </div>
                    </div>
                  </div>

                  {/* Main Date Display */}
                  <div className="text-2xl font-black font-mono tracking-tight mb-3 text-left rtl:text-right">
                    {appt.formattedDate}
                  </div>

                  {/* Grace Period Footer */}
                  <div className={`pt-3 border-t flex items-center justify-between text-xs ${
                    isDark ? 'border-slate-800 text-slate-400' : 'border-slate-100 text-slate-600'
                  }`}>
                    <span className="font-mono">
                      {appt.gracePeriodEnd} – {appt.gracePeriodStart}
                    </span>
                    <div className="flex items-center gap-1 text-slate-500 dark:text-slate-400">
                      <span>{isFirst ? 'مهلة السماح (3 أيام):' : 'مهلة السماح:'}</span>
                      {isFirst ? (
                        <Hourglass className="w-3.5 h-3.5 text-teal-500" />
                      ) : (
                        <History className="w-3.5 h-3.5" />
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        ) : (
          /* Past Biometrics Tab */
          <div className="space-y-3">
            <div className="text-right text-xs font-bold text-slate-500 dark:text-slate-400">
              سجل التبصيمات السابقة الموثقة
            </div>

            {historyLogs.map((log) => (
              <div
                key={log.id}
                className={`w-full rounded-2xl p-4 border shadow-sm transition-all text-right ${
                  isDark ? 'bg-[#071928] border-slate-800' : 'bg-white border-slate-200'
                }`}
              >
                <div className="flex items-center justify-between mb-2">
                  <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-100 dark:bg-emerald-950 text-emerald-800 dark:text-emerald-300 flex items-center gap-1">
                    <CheckCircle2 className="w-3.5 h-3.5" />
                    <span>{log.status}</span>
                  </span>
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-sm">دورة التبصيم #{log.cycleNumber}</span>
                  </div>
                </div>

                <div className="text-lg font-bold font-mono text-left rtl:text-right mb-1">
                  {log.formattedDate}
                </div>

                <div className="text-xs text-slate-500 dark:text-slate-400 mb-2">
                  المرحلة: <strong className="text-teal-600 dark:text-teal-400">{log.dayConfirmed}</strong>
                </div>

                <p className="text-xs text-slate-600 dark:text-slate-300 pt-2 border-t border-slate-100 dark:border-slate-800">
                  {log.note}
                </p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
