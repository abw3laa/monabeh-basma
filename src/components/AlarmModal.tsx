import React, { useEffect, useState } from 'react';
import { ThemeMode } from '../types';
import { playAlarmSound, stopAlarmSound, playHapticSuccess } from '../utils/audio';
import { Check, Clock, VolumeX, AlertCircle, Smartphone, Bell } from 'lucide-react';

interface AlarmModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirmBiometric: () => void;
  onSnooze: () => void;
  theme: ThemeMode;
  dateStr?: string;
  timeStr?: string;
}

export const AlarmModal: React.FC<AlarmModalProps> = ({
  isOpen,
  onClose,
  onConfirmBiometric,
  onSnooze,
  theme,
  dateStr = '12 أكتوبر 2026',
  timeStr = '10:00 صباحاً',
}) => {
  const [isMuted, setIsMuted] = useState(false);
  const [snoozeActive, setSnoozeActive] = useState(false);
  const [snoozeSeconds, setSnoozeSeconds] = useState(3600); // 60 mins

  const isDark = theme === 'dark';

  useEffect(() => {
    if (isOpen && !isMuted && !snoozeActive) {
      playAlarmSound('pulse_alarm');
    }
    return () => {
      stopAlarmSound();
    };
  }, [isOpen, isMuted, snoozeActive]);

  useEffect(() => {
    let timer: number;
    if (snoozeActive && snoozeSeconds > 0) {
      timer = window.setInterval(() => {
        setSnoozeSeconds((s) => {
          if (s <= 1) {
            setSnoozeActive(false);
            return 3600;
          }
          return s - 1;
        });
      }, 1000);
    }
    return () => clearInterval(timer);
  }, [snoozeActive, snoozeSeconds]);

  if (!isOpen) return null;

  const handleMute = () => {
    setIsMuted(true);
    stopAlarmSound();
  };

  const handleSnooze = () => {
    stopAlarmSound();
    setSnoozeActive(true);
    onSnooze();
  };

  const handleConfirm = () => {
    stopAlarmSound();
    playHapticSuccess();
    onConfirmBiometric();
    onClose();
  };

  const formatCountdown = (totalSecs: number) => {
    const mins = Math.floor(totalSecs / 60);
    const secs = totalSecs % 60;
    return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
  };

  return (
    <div className={`fixed inset-0 z-50 overflow-y-auto flex flex-col justify-between p-4 sm:p-6 transition-colors duration-300 ${
      isDark ? 'bg-[#030d17] text-white' : 'bg-[#fbfcfd] text-slate-900'
    }`}>
      {/* Top Bar status pills */}
      <div className="flex items-center justify-between w-full max-w-md mx-auto pt-4">
        {/* Continuous sound & vibration */}
        <div className={`flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-medium border ${
          isDark ? 'bg-[#0c1c2b] text-slate-300 border-slate-800' : 'bg-slate-100 text-slate-700 border-slate-200'
        }`}>
          <Smartphone className="w-3.5 h-3.5 text-teal-500 animate-pulse" />
          <span>صوت + اهتزاز مستمر</span>
        </div>

        {/* Alarm is active now */}
        <div className={`flex items-center gap-1.5 px-3.5 py-1 rounded-full text-xs font-semibold border ${
          isDark
            ? 'bg-teal-950/60 text-teal-300 border-teal-800'
            : 'bg-red-50 text-red-700 border-red-200'
        }`}>
          <Bell className="w-3.5 h-3.5 text-red-500 animate-bounce" />
          <span>منبه البصمة نشط الآن</span>
        </div>
      </div>

      {/* Center Biometric Alarm Graphic & Time */}
      <div className="flex flex-col items-center justify-center my-auto py-6 max-w-md mx-auto w-full text-center">
        {/* Pulsing Concentric Radar Rings */}
        <div className="relative flex items-center justify-center my-6">
          <div className="absolute w-48 h-48 rounded-full border border-red-500/20 animate-radar-ripple" />
          <div className="absolute w-40 h-40 rounded-full border border-red-500/40 animate-pulse-ring" />
          <div className="absolute w-32 h-32 rounded-full bg-red-500/10 dark:bg-red-950/40 blur-md" />
          
          <div className="relative w-24 h-24 rounded-full bg-gradient-to-br from-red-600 to-rose-700 shadow-xl flex items-center justify-center border-4 border-white/20">
            {/* Alarm bell + fingerprint rings emblem */}
            <div className="relative flex items-center justify-center">
              <span className="material-symbols-outlined text-[42px] text-white animate-pulse" style={{ fontVariationSettings: "'FILL' 1" }}>
                notifications_active
              </span>
            </div>
          </div>
        </div>

        {/* Digital Time */}
        <h1 className="text-5xl font-black tracking-tight mb-1 font-mono">
          {timeStr}
        </h1>

        <div className="flex items-center gap-1.5 text-xs font-medium opacity-80 mb-3">
          <span className="material-symbols-outlined text-[16px]">calendar_today</span>
          <span>الإثنين، {dateStr}</span>
        </div>

        <h2 className="text-2xl font-extrabold tracking-tight mb-6">
          حان موعد البصمة
        </h2>

        {/* 3-Day Window Tracking Card */}
        <div className={`w-full rounded-2xl p-4 text-right mb-4 border transition-all ${
          isDark
            ? 'bg-[#0a1926] border-slate-800 text-slate-100 shadow-lg'
            : 'bg-white border-slate-200 text-slate-800 shadow-sm'
        }`}>
          <div className="flex items-center justify-between mb-3">
            <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-red-100 dark:bg-red-950/80 text-red-600 dark:text-red-300">
              اليوم الأول من 3
            </span>
            <div className="flex items-center gap-1.5 font-bold text-sm">
              <span className="w-2.5 h-2.5 rounded-full bg-red-500 animate-ping" />
              <span>نافذة الـ 3 أيام المتاحة</span>
            </div>
          </div>

          {/* 3 Progress Bars */}
          <div className="grid grid-cols-3 gap-2 text-center text-xs mb-3">
            <div className="flex flex-col items-center">
              <div className="w-full h-2 rounded-full bg-red-500 shadow-sm shadow-red-500/50 mb-1.5" />
              <span className="font-bold text-red-600 dark:text-red-400">اليوم 1 (نشط)</span>
              <span className="text-[10px] opacity-75">12 أكتوبر</span>
            </div>
            <div className="flex flex-col items-center">
              <div className={`w-full h-2 rounded-full mb-1.5 ${isDark ? 'bg-slate-700' : 'bg-slate-200'}`} />
              <span className="font-semibold opacity-70">اليوم 2 (تذكير)</span>
              <span className="text-[10px] opacity-75">13 أكتوبر</span>
            </div>
            <div className="flex flex-col items-center">
              <div className={`w-full h-2 rounded-full mb-1.5 ${isDark ? 'bg-slate-700' : 'bg-slate-200'}`} />
              <span className="font-semibold opacity-70">اليوم 3 (الأخير)</span>
              <span className="text-[10px] opacity-75">14 أكتوبر</span>
            </div>
          </div>

          <div className="pt-2 border-t border-slate-200/50 dark:border-slate-800 flex items-center justify-between text-xs text-slate-500 dark:text-slate-400">
            <span>12 – 14 أكتوبر</span>
            <div className="flex items-center gap-1 text-teal-600 dark:text-teal-400 font-semibold">
              <Clock className="w-3.5 h-3.5" />
              <span>متبقي على إغلاق اليوم الأول: 13 ساعة</span>
            </div>
          </div>
        </div>

        {/* Warning Callout */}
        <div className={`w-full rounded-xl p-3 text-right flex items-start gap-2.5 text-xs leading-relaxed border mb-6 ${
          isDark
            ? 'bg-red-950/20 border-red-900/40 text-red-300'
            : 'bg-red-50 border-red-200 text-red-800'
        }`}>
          <AlertCircle className="w-4 h-4 text-red-500 shrink-0 mt-0.5" />
          <p>
            <strong className="font-bold">تنبيه:</strong> إيقاف صوت المنبه لا يعتبر تأكيداً لحضور الموعد. لإثبات حضورك وانتقال المعاملة اضغط زر (تمت البصمة).
          </p>
        </div>

        {/* Snooze countdown notification if active */}
        {snoozeActive && (
          <div className="w-full mb-4 p-3 rounded-xl bg-teal-500/10 border border-teal-500/30 text-teal-400 text-xs text-center font-bold">
            تم تفعيل الغفوة • سيعاد الرنين تلقائياً بعد {formatCountdown(snoozeSeconds)}
          </div>
        )}
      </div>

      {/* Action Buttons */}
      <div className="w-full max-w-md mx-auto space-y-3 pb-4">
        {/* Confirm Attendance Button */}
        <button
          onClick={handleConfirm}
          className="w-full h-14 rounded-2xl bg-[#006a61] hover:bg-[#00574f] text-white font-bold text-base shadow-lg shadow-teal-900/20 active:scale-98 transition-all flex items-center justify-center gap-2 cursor-pointer"
        >
          <Check className="w-5 h-5 stroke-[3]" />
          <span>تمت البصمة</span>
        </button>

        {/* Snooze 60 min */}
        <button
          onClick={handleSnooze}
          className={`w-full h-12 rounded-2xl font-bold text-sm border flex items-center justify-center gap-2 transition-all cursor-pointer ${
            isDark
              ? 'bg-[#0f2537] border-slate-700 text-teal-300 hover:bg-[#163750]'
              : 'bg-white border-teal-200 text-[#006a61] hover:bg-teal-50'
          }`}
        >
          <Clock className="w-4 h-4" />
          <span>ذكرني لاحقاً</span>
        </button>

        {/* Mute Only Button */}
        {!isMuted ? (
          <button
            onClick={handleMute}
            className="w-full py-2 text-xs font-medium text-slate-500 dark:text-slate-400 hover:underline flex items-center justify-center gap-1.5 cursor-pointer"
          >
            <VolumeX className="w-4 h-4" />
            <span>إيقاف الصوت فقط مع إبقاء التذكير على الشاشة</span>
          </button>
        ) : (
          <div className="text-center text-xs text-teal-500 font-semibold py-1">
            تم كتم الصوت • الشاشة نشطة حتى تؤكد موعدك
          </div>
        )}

        <p className="text-[11px] text-center text-slate-400 dark:text-slate-500">
          في حال التأجيل، سيعاد تشغيل الرنين تلقائياً بعد 60 دقيقة.
        </p>
      </div>
    </div>
  );
};
