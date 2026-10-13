import React, { useState } from 'react';
import { ThemeMode } from '../types';
import { X, HelpCircle, Calendar, Clock, Check, Plus } from 'lucide-react';
import { generate28DaySchedule } from '../utils/date';

interface NewAppointmentModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSaveSchedule: (startDateStr: string, cadenceDays: number) => void;
  onOpenInstructions: () => void;
  theme: ThemeMode;
}

export const NewAppointmentModal: React.FC<NewAppointmentModalProps> = ({
  isOpen,
  onClose,
  onSaveSchedule,
  onOpenInstructions,
  theme,
}) => {
  const [startDate, setStartDate] = useState('2026-10-12');
  const [alarmTime, setAlarmTime] = useState('10:00');
  const [cadenceDays, setCadenceDays] = useState(28);

  if (!isOpen) return null;
  const isDark = theme === 'dark';

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    onSaveSchedule(startDate, cadenceDays);
    onClose();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-sm animate-in fade-in duration-200">
      <div
        className={`w-full max-w-md rounded-t-3xl sm:rounded-3xl p-5 sm:p-6 shadow-2xl transition-all border ${
          isDark
            ? 'bg-[#0a1926] border-slate-700 text-white'
            : 'bg-white border-slate-200 text-slate-900'
        }`}
      >
        {/* Header */}
        <div className="flex items-center justify-between mb-4">
          <button
            onClick={onClose}
            className={`p-2 rounded-full transition-colors cursor-pointer ${
              isDark ? 'hover:bg-slate-800 text-slate-400' : 'hover:bg-slate-100 text-slate-500'
            }`}
          >
            <X className="w-5 h-5" />
          </button>

          <div className="flex items-center gap-2">
            <h3 className="text-lg font-bold">تسجيل بصمة جديدة</h3>
            <button
              onClick={onOpenInstructions}
              className="p-1 rounded-full text-teal-600 dark:text-teal-400 hover:bg-teal-50 dark:hover:bg-teal-950 transition-colors cursor-pointer"
              title="تعليمات الإعداد"
            >
              <HelpCircle className="w-5 h-5" />
            </button>
          </div>
        </div>

        <form onSubmit={handleSave} className="space-y-4">
          {/* Start Date */}
          <div className="text-right">
            <label className="block text-xs font-bold mb-1.5 flex items-center justify-end gap-1.5 text-slate-700 dark:text-slate-300">
              <span>تاريخ أول بصمة / الموعد القادم (اليوم الأول)</span>
              <Calendar className="w-4 h-4 text-teal-500" />
            </label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              required
              className={`w-full p-3 rounded-xl border text-sm font-semibold text-right outline-none focus:ring-2 focus:ring-teal-500 ${
                isDark
                  ? 'bg-[#0f2537] border-slate-700 text-white'
                  : 'bg-slate-50 border-slate-200 text-slate-900'
              }`}
            />
            <span className="text-[11px] text-slate-400 block mt-1">
              مطابق لتاريخ بدء اليوم الأول المعتمد في نظام Gocbil الرسمي
            </span>
          </div>

          {/* Alarm Time */}
          <div className="text-right">
            <label className="block text-xs font-bold mb-1.5 flex items-center justify-end gap-1.5 text-slate-700 dark:text-slate-300">
              <span>وقت انطلاق التنبيه</span>
              <Clock className="w-4 h-4 text-teal-500" />
            </label>
            <input
              type="time"
              value={alarmTime}
              onChange={(e) => setAlarmTime(e.target.value)}
              className={`w-full p-3 rounded-xl border text-sm font-semibold text-right outline-none focus:ring-2 focus:ring-teal-500 ${
                isDark
                  ? 'bg-[#0f2537] border-slate-700 text-white'
                  : 'bg-slate-50 border-slate-200 text-slate-900'
              }`}
            />
          </div>

          {/* Cadence Selection */}
          <div className="text-right">
            <label className="block text-xs font-bold mb-1.5 text-slate-700 dark:text-slate-300">
              دورة التكرار القانونية
            </label>
            <div className="grid grid-cols-2 gap-2">
              <button
                type="button"
                onClick={() => setCadenceDays(28)}
                className={`p-3 rounded-xl border text-xs font-bold flex items-center justify-between cursor-pointer ${
                  cadenceDays === 28
                    ? 'border-teal-500 bg-teal-500/10 text-teal-700 dark:text-teal-300'
                    : isDark
                    ? 'border-slate-800 bg-[#0f2537] text-slate-400'
                    : 'border-slate-200 bg-slate-50 text-slate-600'
                }`}
              >
                {cadenceDays === 28 && <Check className="w-4 h-4 text-teal-500" />}
                <span>كل 28 يوماً (المهلة الرسمية)</span>
              </button>

              <button
                type="button"
                onClick={() => setCadenceDays(7)}
                className={`p-3 rounded-xl border text-xs font-bold flex items-center justify-between cursor-pointer ${
                  cadenceDays === 7
                    ? 'border-teal-500 bg-teal-500/10 text-teal-700 dark:text-teal-300'
                    : isDark
                    ? 'border-slate-800 bg-[#0f2537] text-slate-400'
                    : 'border-slate-200 bg-slate-50 text-slate-600'
                }`}
              >
                {cadenceDays === 7 && <Check className="w-4 h-4 text-teal-500" />}
                <span>أسبوعي (كل 7 أيام)</span>
              </button>
            </div>
          </div>

          {/* Submit Button */}
          <button
            type="submit"
            className="w-full py-3.5 rounded-xl bg-[#006a61] hover:bg-[#00574f] text-white font-bold text-sm shadow-md active:scale-98 transition-all flex items-center justify-center gap-2 mt-4 cursor-pointer"
          >
            <Plus className="w-4 h-4" />
            <span>حفظ وجدولة المواعيد تلقائياً</span>
          </button>
        </form>
      </div>
    </div>
  );
};
