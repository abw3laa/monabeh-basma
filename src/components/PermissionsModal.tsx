import React from 'react';
import { AppSettings, ThemeMode } from '../types';
import { X, ShieldCheck, ArrowRight, Check } from 'lucide-react';

interface PermissionsModalProps {
  isOpen: boolean;
  onClose: () => void;
  settings: AppSettings;
  onUpdateSettings: (newSettings: Partial<AppSettings>) => void;
  theme: ThemeMode;
}

export const PermissionsModal: React.FC<PermissionsModalProps> = ({
  isOpen,
  onClose,
  settings,
  onUpdateSettings,
  theme,
}) => {
  if (!isOpen) return null;
  const isDark = theme === 'dark';

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
        <div className="flex items-center justify-between mb-5">
          <button
            onClick={onClose}
            className={`p-2 rounded-full transition-colors cursor-pointer ${
              isDark ? 'hover:bg-slate-800 text-slate-400' : 'hover:bg-slate-100 text-slate-500'
            }`}
          >
            <X className="w-5 h-5" />
          </button>

          <div className="flex items-center gap-2">
            <h3 className="text-lg font-bold">الأذونات و الإشعارات</h3>
            <div className="w-8 h-8 rounded-full bg-teal-50 dark:bg-teal-900/50 text-teal-600 dark:text-teal-400 flex items-center justify-center">
              <ShieldCheck className="w-5 h-5" />
            </div>
          </div>
        </div>

        {/* Permissions List */}
        <div className="space-y-3 mb-6">
          {/* Permission 1: Display Over Other Apps */}
          <div
            className={`p-4 rounded-2xl flex items-center justify-between gap-3 border transition-all ${
              isDark ? 'bg-[#0f2537] border-slate-800' : 'bg-slate-50 border-slate-100'
            }`}
          >
            <button
              onClick={() =>
                onUpdateSettings({ displayOverAppsGranted: !settings.displayOverAppsGranted })
              }
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 cursor-pointer ${
                settings.displayOverAppsGranted
                  ? 'bg-teal-100 dark:bg-teal-950/80 text-teal-800 dark:text-teal-300 border border-teal-300 dark:border-teal-700'
                  : 'bg-[#006a61] text-white hover:bg-[#00524b]'
              }`}
            >
              {settings.displayOverAppsGranted ? (
                <>
                  <Check className="w-4 h-4" />
                  <span>مفعّل</span>
                </>
              ) : (
                <>
                  <ArrowRight className="w-4 h-4 rtl:-scale-x-100" />
                  <span>تفعيل</span>
                </>
              )}
            </button>
            <div className="text-right flex-1">
              <h4 className="font-bold text-sm">الظهور فوق التطبيقات</h4>
              <p className={`text-xs mt-0.5 ${isDark ? 'text-slate-400' : 'text-slate-500'}`}>
                لعرض نافذة الإنذار الفورية حتى أثناء القفل
              </p>
            </div>
          </div>

          {/* Permission 2: Critical Notifications */}
          <div
            className={`p-4 rounded-2xl flex items-center justify-between gap-3 border transition-all ${
              isDark ? 'bg-[#0f2537] border-slate-800' : 'bg-slate-50 border-slate-100'
            }`}
          >
            <button
              onClick={() =>
                onUpdateSettings({ criticalNotificationsGranted: !settings.criticalNotificationsGranted })
              }
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 cursor-pointer ${
                settings.criticalNotificationsGranted
                  ? 'bg-teal-100 dark:bg-teal-950/80 text-teal-800 dark:text-teal-300 border border-teal-300 dark:border-teal-700'
                  : 'bg-[#006a61] text-white hover:bg-[#00524b]'
              }`}
            >
              {settings.criticalNotificationsGranted ? (
                <>
                  <Check className="w-4 h-4" />
                  <span>مفعّل</span>
                </>
              ) : (
                <>
                  <ArrowRight className="w-4 h-4 rtl:-scale-x-100" />
                  <span>تفعيل</span>
                </>
              )}
            </button>
            <div className="text-right flex-1">
              <h4 className="font-bold text-sm">الإشعارات المهمة</h4>
              <p className={`text-xs mt-0.5 ${isDark ? 'text-slate-400' : 'text-slate-500'}`}>
                تخطي نمط عدم الإزعاج أثناء مهلة البصمة
              </p>
            </div>
          </div>

          {/* Permission 3: Battery Optimization Ignore */}
          <div
            className={`p-4 rounded-2xl flex items-center justify-between gap-3 border transition-all ${
              isDark ? 'bg-[#0f2537] border-slate-800' : 'bg-slate-50 border-slate-100'
            }`}
          >
            <button
              onClick={() =>
                onUpdateSettings({ batteryExemptionGranted: !settings.batteryExemptionGranted })
              }
              className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 cursor-pointer ${
                settings.batteryExemptionGranted
                  ? 'bg-teal-100 dark:bg-teal-950/80 text-teal-800 dark:text-teal-300 border border-teal-300 dark:border-teal-700'
                  : 'bg-[#006a61] text-white hover:bg-[#00524b]'
              }`}
            >
              {settings.batteryExemptionGranted ? (
                <>
                  <Check className="w-4 h-4" />
                  <span>مفعّل</span>
                </>
              ) : (
                <>
                  <ArrowRight className="w-4 h-4 rtl:-scale-x-100" />
                  <span>تفعيل</span>
                </>
              )}
            </button>
            <div className="text-right flex-1">
              <h4 className="font-bold text-sm">استثناء توفير البطارية</h4>
              <p className={`text-xs mt-0.5 ${isDark ? 'text-slate-400' : 'text-slate-500'}`}>
                منع نظام التشغيل من قتل تطبيق التنبيه
              </p>
            </div>
          </div>
        </div>

        {/* Done Button */}
        <button
          onClick={onClose}
          className="w-full py-3 rounded-xl bg-[#0f2537] text-white font-bold text-sm hover:bg-[#1a3850] active:scale-98 transition-all cursor-pointer"
        >
          حفظ وإغلاق
        </button>
      </div>
    </div>
  );
};
