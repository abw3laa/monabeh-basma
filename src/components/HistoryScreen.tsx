import React from 'react';
import { BiometricLog, ThemeMode } from '../types';
import { CheckCircle2, History, Award, CalendarCheck, ShieldCheck } from 'lucide-react';

interface HistoryScreenProps {
  historyLogs: BiometricLog[];
  theme: ThemeMode;
  onOpenPermissions: () => void;
}

export const HistoryScreen: React.FC<HistoryScreenProps> = ({
  historyLogs,
  theme,
  onOpenPermissions,
}) => {
  const isDark = theme === 'dark';

  return (
    <div className={`w-full min-h-[calc(100vh-8rem)] px-4 pb-24 pt-2 transition-colors ${
      isDark ? 'bg-[#000f1d] text-white' : 'bg-[#f7f9fb] text-[#191c1e]'
    }`}>
      <div className="max-w-md mx-auto w-full space-y-4">
        {/* Header Bar */}
        <div className="flex items-center justify-between pt-1">
          <div className="flex items-center gap-2">
            <History className="w-5 h-5 text-[#006a61] dark:text-teal-400" />
            <h1 className="text-xl font-bold tracking-tight">سجل التبصيم</h1>
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

        {/* Reliability / Compliance Stat Cards */}
        <div className="grid grid-cols-2 gap-3">
          <div className={`p-4 rounded-2xl border text-right ${
            isDark ? 'bg-[#071928] border-slate-800' : 'bg-white border-slate-200 shadow-sm'
          }`}>
            <div className="flex items-center justify-end gap-1.5 text-xs text-slate-500 dark:text-slate-400 mb-1">
              <span>نسبة الالتزام</span>
              <Award className="w-4 h-4 text-emerald-500" />
            </div>
            <div className="text-2xl font-black text-emerald-600 dark:text-emerald-400 font-mono">
              ١٠٠٪
            </div>
            <p className="text-[10px] text-slate-400 mt-1">بدون أي تأخير أو مخالفات</p>
          </div>

          <div className={`p-4 rounded-2xl border text-right ${
            isDark ? 'bg-[#071928] border-slate-800' : 'bg-white border-slate-200 shadow-sm'
          }`}>
            <div className="flex items-center justify-end gap-1.5 text-xs text-slate-500 dark:text-slate-400 mb-1">
              <span>الدورات المكتملة</span>
              <CalendarCheck className="w-4 h-4 text-teal-500" />
            </div>
            <div className="text-2xl font-black font-mono">
              {historyLogs.length}
            </div>
            <p className="text-[10px] text-slate-400 mt-1">توثيق رسمي مسجل</p>
          </div>
        </div>

        {/* Audit Log Banner */}
        <div className={`p-3.5 rounded-2xl border flex items-center justify-between text-xs ${
          isDark ? 'bg-teal-950/40 border-teal-800 text-teal-300' : 'bg-teal-50 border-teal-200 text-teal-900'
        }`}>
          <ShieldCheck className="w-5 h-5 text-teal-600 dark:text-teal-400 shrink-0" />
          <div className="text-right flex-1 pr-2">
            <strong>سجل أمني مشفر محلياً:</strong> يوثق حضورك في المواعيد القانونية لمطابقة قيود دائرة الهجرة الرسمية.
          </div>
        </div>

        {/* List of Verified Logs */}
        <div className="space-y-3">
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
                <span className="font-bold text-sm">الدورة #{log.cycleNumber}</span>
              </div>

              <div className="text-lg font-black font-mono mb-1">
                {log.formattedDate}
              </div>

              <div className="text-xs text-slate-500 dark:text-slate-400 mb-2">
                التأكيد في: <strong className="text-teal-600 dark:text-teal-400">{log.dayConfirmed}</strong>
              </div>

              <p className="text-xs text-slate-600 dark:text-slate-300 pt-2 border-t border-slate-100 dark:border-slate-800">
                {log.note}
              </p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
