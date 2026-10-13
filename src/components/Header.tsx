import React from 'react';
import { ThemeMode } from '../types';
import { Sun, Moon, BellRing, CodeXml } from 'lucide-react';

interface HeaderProps {
  theme: ThemeMode;
  onToggleTheme: () => void;
  onTriggerAlarm: () => void;
  onOpenKotlinCode: () => void;
}

export const Header: React.FC<HeaderProps> = ({
  theme,
  onToggleTheme,
  onTriggerAlarm,
  onOpenKotlinCode,
}) => {
  const isDark = theme === 'dark';

  return (
    <header className={`sticky top-0 w-full z-40 backdrop-blur-md transition-colors ${
      isDark ? 'bg-[#000f1d]/90 text-white border-b border-gray-800' : 'bg-[#f7f9fb]/90 text-[#191c1e] border-b border-gray-200/60'
    }`}>
      {/* Android Status Bar */}
      <div className="h-10 px-4 flex items-center justify-between text-xs font-semibold select-none">
        <span className="tracking-wide">٠٩:٤١</span>
        <div className="flex items-center gap-1.5 opacity-80">
          <span className="material-symbols-outlined text-[16px]">signal_cellular_alt</span>
          <span className="material-symbols-outlined text-[16px]">wifi</span>
          <span className="material-symbols-outlined text-[16px]">battery_full</span>
        </div>
      </div>

      {/* Action / Tools Sub-bar */}
      <div className="px-4 py-2 flex items-center justify-between gap-2">
        <div className="flex items-center gap-2">
          <button
            onClick={onToggleTheme}
            title={isDark ? 'التحويل للوضع الفاتح' : 'التحويل للوضع الداكن'}
            className={`p-2 rounded-xl transition-all flex items-center gap-1.5 text-xs font-medium cursor-pointer ${
              isDark ? 'bg-[#0f2537] text-teal-300 hover:bg-[#16334d]' : 'bg-white text-slate-700 hover:bg-slate-100 shadow-sm'
            }`}
          >
            {isDark ? <Sun className="w-4 h-4 text-amber-400" /> : <Moon className="w-4 h-4 text-slate-700" />}
            <span className="hidden sm:inline">{isDark ? 'فاتح' : 'داكن'}</span>
          </button>

          <button
            onClick={onOpenKotlinCode}
            title="عرض كود Kotlin"
            className={`px-2.5 py-1.5 rounded-xl transition-all flex items-center gap-1.5 text-xs font-medium cursor-pointer ${
              isDark ? 'bg-[#0f2537] text-teal-300 hover:bg-[#16334d] border border-teal-500/20' : 'bg-teal-50 text-teal-800 hover:bg-teal-100 border border-teal-200'
            }`}
          >
            <CodeXml className="w-4 h-4 text-teal-600 dark:text-teal-400" />
            <span className="font-semibold">كود Kotlin</span>
          </button>

          <a
            href="/basma-alarm-project.zip"
            download="basma-alarm-project.zip"
            title="تحميل المشروع كاملاً بملف مضغوط (.zip)"
            className={`px-2.5 py-1.5 rounded-xl transition-all flex items-center gap-1.5 text-xs font-bold shadow-sm cursor-pointer ${
              isDark
                ? 'bg-emerald-950/80 hover:bg-emerald-900 text-emerald-300 border border-emerald-500/40'
                : 'bg-emerald-50 hover:bg-emerald-100 text-emerald-800 border border-emerald-300'
            }`}
          >
            <span className="material-symbols-outlined text-[16px]">folder_zip</span>
            <span>تحميل .ZIP</span>
          </a>
        </div>

        {/* Live Alarm Simulator Button */}
        <button
          onClick={onTriggerAlarm}
          className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-semibold bg-red-600 hover:bg-red-700 text-white shadow-sm active:scale-95 transition-all cursor-pointer"
          title="محاكاة رنين المنبه الفعلي"
        >
          <BellRing className="w-3.5 h-3.5 animate-bounce" />
          <span>تجربة المنبه</span>
        </button>
      </div>
    </header>
  );
};
