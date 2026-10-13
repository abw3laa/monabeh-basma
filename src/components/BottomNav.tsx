import React from 'react';
import { TabType, ThemeMode } from '../types';

interface BottomNavProps {
  activeTab: TabType;
  onChangeTab: (tab: TabType) => void;
  theme: ThemeMode;
}

export const BottomNav: React.FC<BottomNavProps> = ({
  activeTab,
  onChangeTab,
  theme,
}) => {
  const isDark = theme === 'dark';

  const navItems: { id: TabType; label: string; icon: string }[] = [
    { id: 'home', label: 'الرئيسية', icon: 'home' },
    { id: 'schedule', label: 'المواعيد', icon: 'event' },
    { id: 'history', label: 'السجل', icon: 'history' },
    { id: 'settings', label: 'الإعدادات', icon: 'settings' },
  ];

  return (
    <nav className={`fixed bottom-0 w-full z-50 transition-colors ${
      isDark
        ? 'bg-[#0c141d]/95 backdrop-blur-md border-t border-slate-800 shadow-[0_-4px_20px_rgba(0,0,0,0.5)]'
        : 'bg-white/95 backdrop-blur-md border-t border-slate-200 shadow-[0_-4px_20px_rgba(0,0,0,0.06)]'
    }`}>
      <div className="max-w-md mx-auto flex justify-around items-center h-20 px-2">
        {navItems.map((item) => {
          const isActive = activeTab === item.id;
          return (
            <button
              key={item.id}
              type="button"
              onClick={() => onChangeTab(item.id)}
              className={`flex flex-col items-center justify-center gap-1 min-w-[64px] min-h-[44px] py-1 transition-all select-none cursor-pointer group ${
                isActive
                  ? isDark
                    ? 'text-teal-400 font-bold'
                    : 'text-[#006a61] font-bold'
                  : 'text-slate-400 hover:text-slate-600 dark:hover:text-slate-200'
              }`}
            >
              <div
                className={`w-14 h-8 rounded-full flex items-center justify-center transition-all group-active:scale-95 ${
                  isActive
                    ? isDark
                      ? 'bg-teal-500/20 text-teal-300 shadow-[0_0_10px_rgba(20,184,166,0.3)]'
                      : 'bg-teal-50 text-[#006a61] shadow-sm'
                    : ''
                }`}
              >
                <span className="material-symbols-outlined text-[24px]">{item.icon}</span>
              </div>
              <span className="text-xs">{item.label}</span>
            </button>
          );
        })}
      </div>
    </nav>
  );
};
