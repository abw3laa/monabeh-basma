import React, { useState } from 'react';
import { AppSettings, ThemeMode } from '../types';

interface SettingsScreenProps {
  settings: AppSettings;
  onUpdateSettings: (newSettings: Partial<AppSettings>) => void;
  onOpenWelcome: () => void;
  onOpenKotlinCode: () => void;
  onToggleTheme: () => void;
  theme: ThemeMode;
}

export const SettingsScreen: React.FC<SettingsScreenProps> = ({
  settings,
  onUpdateSettings,
  onOpenWelcome,
  onOpenKotlinCode,
  onToggleTheme,
  theme,
}) => {
  const isDark = theme === 'dark';

  // Modals state matching the exact HTML modals
  const [activeModal, setActiveModal] = useState<
    'lang' | 'alarm' | 'perms' | 'about-app' | 'about-dev' | 'disclaimer' | null
  >(null);

  // Selected language state
  const [selectedLang, setSelectedLang] = useState<'ar' | 'tr' | 'en'>('ar');

  const closeModal = () => setActiveModal(null);

  return (
    <div className={`relative w-full min-h-[calc(100vh-4rem)] pb-36 pt-2 transition-colors ${
      isDark ? 'bg-[#080e14] text-slate-100' : 'bg-[#f7f9fb] text-[#191c1e]'
    }`}>
      {/* Header matching HTML */}
      <div className="max-w-md mx-auto w-full px-4 mb-4">
        <div className="flex items-center justify-between pt-1">
          <div className="flex items-center gap-3">
            <div className={`w-11 h-11 rounded-2xl flex items-center justify-center shadow-sm ${
              isDark
                ? 'bg-teal-500/15 border border-teal-500/30 text-teal-400'
                : 'bg-teal-50 border border-teal-200 text-[#006a61]'
            }`}>
              <span className="material-symbols-outlined text-[24px]">settings</span>
            </div>
            <span className={`text-lg font-bold tracking-tight ${isDark ? 'text-white' : 'text-slate-900'}`}>
              الإعدادات
            </span>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={() => setActiveModal('perms')}
              aria-label="رمز البصمة والتحقق"
              className={`w-11 h-11 flex items-center justify-center rounded-2xl transition-colors active:scale-95 shadow-[0_0_12px_rgba(20,184,166,0.15)] cursor-pointer ${
                isDark
                  ? 'bg-[#121c26] border border-teal-500/30 text-teal-400 hover:bg-[#182635]'
                  : 'bg-white border border-teal-200 text-[#006a61] hover:bg-teal-50'
              }`}
            >
              <span className="material-symbols-outlined text-[24px]">fingerprint</span>
            </button>
          </div>
        </div>
      </div>

      {/* Main List of Settings Buttons matching user's exact HTML */}
      <div className="max-w-md mx-auto w-full px-4 space-y-3.5">
        {/* 1. اللغة */}
        <button
          type="button"
          onClick={() => setActiveModal('lang')}
          className={`w-full p-4 rounded-2xl shadow-sm border flex items-center justify-between text-start transition-all cursor-pointer active:scale-95 ${
            isDark
              ? 'bg-[#121c26] border-teal-900/40 hover:bg-[#162230]'
              : 'bg-white border-slate-200 hover:bg-slate-50'
          }`}
        >
          <div className="flex items-center gap-3.5 min-w-0">
            <div className="w-11 h-11 rounded-2xl bg-teal-500/15 border border-teal-500/20 text-teal-400 flex items-center justify-center shrink-0 shadow-sm">
              <span className="material-symbols-outlined text-[22px]">language</span>
            </div>
            <div className="flex flex-col min-w-0 text-right">
              <span className={`font-bold text-sm ${isDark ? 'text-white' : 'text-slate-900'}`}>اللغة</span>
              <span className="text-xs text-slate-400 truncate mt-0.5">تحديد لغة واجهة التطبيق</span>
            </div>
          </div>
          <div className="flex items-center gap-2 text-slate-400 shrink-0">
            <span className={`text-xs font-semibold px-3 py-1 rounded-full border ${
              isDark
                ? 'text-teal-300 bg-teal-950/80 border-teal-500/30'
                : 'text-[#006a61] bg-teal-50 border-teal-200'
            }`}>
              {selectedLang === 'ar' ? 'العربية' : selectedLang === 'tr' ? 'Türkçe' : 'English'}
            </span>
            <span className="material-symbols-outlined text-[20px] text-slate-500 rtl:rotate-0">chevron_left</span>
          </div>
        </button>

        {/* 2. المظهر */}
        <div
          className={`w-full p-4 rounded-2xl shadow-sm border flex items-center justify-between text-start ${
            isDark ? 'bg-[#121c26] border-teal-900/40' : 'bg-white border-slate-200'
          }`}
        >
          <div className="flex items-center gap-3.5 min-w-0">
            <div className="w-11 h-11 rounded-2xl bg-teal-500/15 border border-teal-500/20 text-teal-400 flex items-center justify-center shrink-0 shadow-sm">
              <span className="material-symbols-outlined text-[22px]">palette</span>
            </div>
            <div className="flex flex-col min-w-0 text-right">
              <span className={`font-bold text-sm ${isDark ? 'text-white' : 'text-slate-900'}`}>المظهر</span>
              <span className="text-xs text-slate-400 truncate mt-0.5">التبديل بين الوضع الفاتح والداكن</span>
            </div>
          </div>
          <button
            type="button"
            onClick={onToggleTheme}
            className={`flex items-center gap-2 px-3 py-1.5 rounded-full border cursor-pointer transition-all active:scale-95 shrink-0 ${
              isDark
                ? 'bg-teal-900/60 border-teal-500/40 text-teal-300 hover:bg-teal-900/80'
                : 'bg-teal-50 border-teal-300 text-[#006a61] hover:bg-teal-100'
            }`}
          >
            <span className="text-xs font-bold">{isDark ? 'داكن' : 'فاتح'}</span>
            <span className="material-symbols-outlined text-[20px] text-teal-400">
              {isDark ? 'toggle_off' : 'toggle_on'}
            </span>
          </button>
        </div>

        {/* 3. إعدادات التنبيه */}
        <button
          type="button"
          onClick={() => setActiveModal('alarm')}
          className={`w-full p-4 rounded-2xl shadow-sm border flex items-center justify-between text-start transition-all cursor-pointer active:scale-95 ${
            isDark
              ? 'bg-[#121c26] border-teal-900/40 hover:bg-[#162230]'
              : 'bg-white border-slate-200 hover:bg-slate-50'
          }`}
        >
          <div className="flex items-center gap-3.5 min-w-0">
            <div className="w-11 h-11 rounded-2xl bg-teal-500/15 border border-teal-500/20 text-teal-400 flex items-center justify-center shrink-0 shadow-sm">
              <span className="material-symbols-outlined text-[22px]">notifications</span>
            </div>
            <div className="flex flex-col min-w-0 text-right">
              <span className={`font-bold text-sm ${isDark ? 'text-white' : 'text-slate-900'}`}>إعدادات التنبيه</span>
              <span className="text-xs text-slate-400 truncate mt-0.5">الصوت، الاهتزاز، الغفوة، ومهلة التص...</span>
            </div>
          </div>
          <div className="flex items-center gap-2 text-slate-400 shrink-0">
            <span className={`text-xs font-semibold px-3 py-1 rounded-full border ${
              isDark
                ? 'text-teal-300 bg-teal-950/80 border-teal-500/30'
                : 'text-[#006a61] bg-teal-50 border-teal-200'
            }`}>
              مخصص
            </span>
            <span className="material-symbols-outlined text-[20px] text-slate-500">chevron_left</span>
          </div>
        </button>

        {/* 4. الأذونات والإشعارات */}
        <button
          type="button"
          onClick={() => setActiveModal('perms')}
          className={`w-full p-4 rounded-2xl shadow-sm border flex items-center justify-between text-start transition-all cursor-pointer active:scale-95 ${
            isDark
              ? 'bg-[#121c26] border-teal-900/40 hover:bg-[#162230]'
              : 'bg-white border-slate-200 hover:bg-slate-50'
          }`}
        >
          <div className="flex items-center gap-3.5 min-w-0">
            <div className="w-11 h-11 rounded-2xl bg-teal-500/15 border border-teal-500/20 text-teal-400 flex items-center justify-center shrink-0 shadow-sm">
              <span className="material-symbols-outlined text-[22px]">verified_user</span>
            </div>
            <div className="flex flex-col min-w-0 text-right">
              <span className={`font-bold text-sm ${isDark ? 'text-white' : 'text-slate-900'}`}>الأذونات والإشعارات</span>
              <span className="text-xs text-slate-400 truncate mt-0.5">الظهور فوق الشاشة، الإشعارات، والبطارية</span>
            </div>
          </div>
          <div className="flex items-center gap-2 text-slate-400 shrink-0">
            <span className={`text-xs font-semibold px-3 py-1 rounded-full border ${
              isDark
                ? 'text-teal-300 bg-teal-950/80 border-teal-500/30'
                : 'text-[#006a61] bg-teal-50 border-teal-200'
            }`}>
              مفعلة
            </span>
            <span className="material-symbols-outlined text-[20px] text-slate-500">chevron_left</span>
          </div>
        </button>

        {/* 5. حول التطبيق */}
        <button
          type="button"
          onClick={() => setActiveModal('about-app')}
          className={`w-full p-4 rounded-2xl shadow-sm border flex items-center justify-between text-start transition-all cursor-pointer active:scale-95 ${
            isDark
              ? 'bg-[#121c26] border-teal-900/40 hover:bg-[#162230]'
              : 'bg-white border-slate-200 hover:bg-slate-50'
          }`}
        >
          <div className="flex items-center gap-3.5 min-w-0">
            <div className="w-11 h-11 rounded-2xl bg-teal-500/15 border border-teal-500/20 text-teal-400 flex items-center justify-center shrink-0 shadow-sm">
              <span className="material-symbols-outlined text-[22px]">info</span>
            </div>
            <div className="flex flex-col min-w-0 text-right">
              <span className={`font-bold text-sm ${isDark ? 'text-white' : 'text-slate-900'}`}>حول التطبيق</span>
              <span className="text-xs text-slate-400 truncate mt-0.5">الإصدار وتفاصيل منظومة الحماية</span>
            </div>
          </div>
          <div className="flex items-center gap-2 text-slate-400 shrink-0">
            <span className={`text-xs font-semibold px-3 py-1 rounded-full border ${
              isDark
                ? 'text-slate-300 bg-[#1b2735] border-slate-700'
                : 'text-slate-700 bg-slate-100 border-slate-300'
            }`}>
              v2.4.0
            </span>
            <span className="material-symbols-outlined text-[20px] text-slate-500">chevron_left</span>
          </div>
        </button>

        {/* 6. حول المطور */}
        <button
          type="button"
          onClick={() => setActiveModal('about-dev')}
          className={`w-full p-4 rounded-2xl shadow-sm border flex items-center justify-between text-start transition-all cursor-pointer active:scale-95 ${
            isDark
              ? 'bg-[#121c26] border-teal-900/40 hover:bg-[#162230]'
              : 'bg-white border-slate-200 hover:bg-slate-50'
          }`}
        >
          <div className="flex items-center gap-3.5 min-w-0">
            <div className="w-11 h-11 rounded-2xl bg-teal-500/15 border border-teal-500/20 text-teal-400 flex items-center justify-center shrink-0 shadow-sm">
              <span className="material-symbols-outlined text-[22px]">code</span>
            </div>
            <div className="flex flex-col min-w-0 text-right">
              <span className={`font-bold text-sm ${isDark ? 'text-white' : 'text-slate-900'}`}>حول المطور</span>
              <span className="text-xs text-slate-400 truncate mt-0.5">فريق تطوير الأنظمة والاتصال</span>
            </div>
          </div>
          <div className="flex items-center gap-1 text-slate-500 shrink-0">
            <span className="material-symbols-outlined text-[20px]">chevron_left</span>
          </div>
        </button>

        {/* 7. إخلاء المسؤولية */}
        <button
          type="button"
          onClick={() => setActiveModal('disclaimer')}
          className={`w-full p-4 rounded-2xl shadow-sm border flex items-center justify-between text-start transition-all cursor-pointer active:scale-95 ${
            isDark
              ? 'bg-[#121c26] border-teal-900/40 hover:bg-[#162230]'
              : 'bg-white border-slate-200 hover:bg-slate-50'
          }`}
        >
          <div className="flex items-center gap-3.5 min-w-0">
            <div className="w-11 h-11 rounded-2xl bg-teal-500/15 border border-teal-500/20 text-teal-400 flex items-center justify-center shrink-0 shadow-sm">
              <span className="material-symbols-outlined text-[22px]">gavel</span>
            </div>
            <div className="flex flex-col min-w-0 text-right">
              <span className={`font-bold text-sm ${isDark ? 'text-white' : 'text-slate-900'}`}>إخلاء المسؤولية</span>
              <span className="text-xs text-slate-400 truncate mt-0.5">الشروط، الأحكام، وتنبيهات الاستخدام</span>
            </div>
          </div>
          <div className="flex items-center gap-1 text-slate-500 shrink-0">
            <span className="material-symbols-outlined text-[20px]">chevron_left</span>
          </div>
        </button>

        {/* 8. كود مشروع Kotlin لأندرويد */}
        <button
          type="button"
          onClick={onOpenKotlinCode}
          className={`w-full p-4 rounded-2xl shadow-sm border flex items-center justify-between text-start transition-all cursor-pointer active:scale-95 ${
            isDark
              ? 'bg-[#0f2537] border-teal-800/80 hover:bg-[#143249]'
              : 'bg-teal-50/80 border-teal-200 hover:bg-teal-100'
          }`}
        >
          <div className="flex items-center gap-3.5 min-w-0">
            <div className="w-11 h-11 rounded-2xl bg-teal-600 text-white flex items-center justify-center shrink-0 shadow-sm">
              <span className="material-symbols-outlined text-[22px]">developer_mode</span>
            </div>
            <div className="flex flex-col min-w-0 text-right">
              <span className="font-bold text-sm text-[#006a61] dark:text-teal-300">مشروع Kotlin لأندرويد</span>
              <span className="text-xs text-teal-800 dark:text-teal-400 truncate mt-0.5">كود Jetpack Compose كامل قابل للبناء</span>
            </div>
          </div>
          <div className="flex items-center gap-1 text-teal-600 dark:text-teal-400 shrink-0">
            <span className="text-xs font-bold px-2.5 py-1 rounded-full bg-teal-600 text-white">عرض الكود</span>
          </div>
        </button>

        {/* 9. تحميل المشروع كاملاً مضغوط (.ZIP) */}
        <a
          href="/basma-alarm-project.zip"
          download="basma-alarm-project.zip"
          className={`w-full p-4 rounded-2xl shadow-sm border flex items-center justify-between text-start transition-all cursor-pointer active:scale-95 ${
            isDark
              ? 'bg-[#06241b] border-emerald-800/80 hover:bg-[#0a3528]'
              : 'bg-emerald-50 border-emerald-200 hover:bg-emerald-100'
          }`}
        >
          <div className="flex items-center gap-3.5 min-w-0">
            <div className="w-11 h-11 rounded-2xl bg-emerald-600 text-white flex items-center justify-center shrink-0 shadow-sm">
              <span className="material-symbols-outlined text-[22px]">folder_zip</span>
            </div>
            <div className="flex flex-col min-w-0 text-right">
              <span className="font-bold text-sm text-emerald-800 dark:text-emerald-300">تحميل المشروع كاملاً (.ZIP)</span>
              <span className="text-xs text-emerald-700/80 dark:text-emerald-400/80 truncate mt-0.5">ملف مضغوط يشمل الويب وتطبيق أندرويد Kotlin</span>
            </div>
          </div>
          <div className="flex items-center gap-1 text-emerald-600 dark:text-emerald-400 shrink-0">
            <span className="text-xs font-bold px-3 py-1 rounded-full bg-emerald-600 text-white flex items-center gap-1">
              <span className="material-symbols-outlined text-[14px]">download</span>
              <span>تحميل ZIP</span>
            </span>
          </div>
        </a>
      </div>

      {/* DOCKED BOTTOM BAR (Matching screen.png & Image 18): Always stays visible above the bottom navigation bar even when closed! */}
      <div className="fixed bottom-20 left-0 right-0 px-4 z-40 pointer-events-auto">
        <button
          type="button"
          onClick={() => setActiveModal('perms')}
          className={`max-w-md mx-auto w-full py-3.5 px-5 rounded-2xl border flex items-center justify-between shadow-2xl active:scale-98 transition-all cursor-pointer ${
            isDark
              ? 'bg-[#071928] border-teal-800/70 text-white hover:bg-[#0c2438]'
              : 'bg-white border-slate-300 text-slate-900 shadow-slate-300/60 hover:bg-slate-50'
          }`}
          title="عرض الأذونات والإشعارات"
        >
          {/* Left: Down chevron indicator */}
          <span className="material-symbols-outlined text-slate-400 text-[20px]">expand_more</span>

          {/* Right: Title & Shield in Teal Circle */}
          <div className="flex items-center gap-2.5">
            <span className="font-bold text-sm">الأذونات وتفادى الإغلاق</span>
            <div className="w-7 h-7 rounded-full bg-teal-50 dark:bg-teal-950 text-[#006a61] dark:text-teal-400 flex items-center justify-center">
              <span className="material-symbols-outlined text-[18px]">verified_user</span>
            </div>
          </div>
        </button>
      </div>

      {/* MODALS CONTAINER */}
      <div className="relative z-[100]">
        {/* Modal 1: Language */}
        {activeModal === 'lang' && (
          <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4 transition-all duration-300">
            <div className={`w-full max-w-md rounded-t-[28px] sm:rounded-2xl p-6 shadow-xl border flex flex-col gap-4 animate-in fade-in slide-in-from-bottom duration-200 ${
              isDark ? 'bg-[#0c141d] text-slate-100 border-teal-900/40' : 'bg-white text-slate-900 border-slate-200'
            }`}>
              <div className="flex items-center justify-between pb-3 border-b border-slate-800">
                <div className="flex items-center gap-2.5">
                  <div className="w-9 h-9 rounded-xl bg-teal-500/15 text-teal-400 flex items-center justify-center">
                    <span className="material-symbols-outlined text-[20px]">language</span>
                  </div>
                  <h3 className="text-base font-bold">اختر اللغة</h3>
                </div>
                <button
                  type="button"
                  onClick={closeModal}
                  className="w-8 h-8 rounded-full bg-slate-800/60 hover:bg-slate-700 flex items-center justify-center text-slate-400 hover:text-white transition-colors cursor-pointer"
                >
                  <span className="material-symbols-outlined text-[18px]">close</span>
                </button>
              </div>

              <div className="flex flex-col gap-2.5">
                {/* Arabic */}
                <div
                  onClick={() => { setSelectedLang('ar'); closeModal(); }}
                  className={`p-3.5 rounded-xl border-2 flex items-center justify-between cursor-pointer transition-all ${
                    selectedLang === 'ar'
                      ? 'border-teal-500 bg-teal-500/10'
                      : isDark ? 'border-slate-800 bg-[#121c26]' : 'border-slate-200 bg-slate-50'
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <span className="text-lg">🇸🇦</span>
                    <div className="flex flex-col text-right">
                      <span className="font-bold text-sm">العربية</span>
                      <span className="text-xs text-slate-400">اللغة الافتراضية للتطبيق</span>
                    </div>
                  </div>
                  <span className="material-symbols-outlined text-teal-400 text-[22px]">
                    {selectedLang === 'ar' ? 'check_circle' : 'radio_button_unchecked'}
                  </span>
                </div>

                {/* Turkish */}
                <div
                  onClick={() => { setSelectedLang('tr'); closeModal(); }}
                  className={`p-3.5 rounded-xl border flex items-center justify-between cursor-pointer transition-all ${
                    selectedLang === 'tr'
                      ? 'border-teal-500 bg-teal-500/10'
                      : isDark ? 'border-slate-800 bg-[#121c26]' : 'border-slate-200 bg-slate-50'
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <span className="text-lg">🇹🇷</span>
                    <div className="flex flex-col text-right">
                      <span className="font-bold text-sm">Türkçe</span>
                      <span className="text-xs text-slate-400">Türk dili arayüzü</span>
                    </div>
                  </div>
                  <span className="material-symbols-outlined text-teal-400 text-[22px]">
                    {selectedLang === 'tr' ? 'check_circle' : 'radio_button_unchecked'}
                  </span>
                </div>

                {/* English */}
                <div
                  onClick={() => { setSelectedLang('en'); closeModal(); }}
                  className={`p-3.5 rounded-xl border flex items-center justify-between cursor-pointer transition-all ${
                    selectedLang === 'en'
                      ? 'border-teal-500 bg-teal-500/10'
                      : isDark ? 'border-slate-800 bg-[#121c26]' : 'border-slate-200 bg-slate-50'
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <span className="text-lg">🇺🇸</span>
                    <div className="flex flex-col text-right">
                      <span className="font-bold text-sm">English</span>
                      <span className="text-xs text-slate-400">English UI</span>
                    </div>
                  </div>
                  <span className="material-symbols-outlined text-teal-400 text-[22px]">
                    {selectedLang === 'en' ? 'check_circle' : 'radio_button_unchecked'}
                  </span>
                </div>
              </div>

              <button
                type="button"
                onClick={closeModal}
                className="w-full py-3 bg-[#006a61] text-white font-bold rounded-xl shadow-sm hover:opacity-95 transition-all text-center mt-2 cursor-pointer"
              >
                تأكيد وحفظ
              </button>
            </div>
          </div>
        )}

        {/* Modal 2: Alarm Settings (إعدادات التنبيه والمراحل) */}
        {activeModal === 'alarm' && (
          <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4 transition-all duration-300 max-h-screen overflow-y-auto">
            <div className={`w-full max-w-md rounded-t-[28px] sm:rounded-2xl p-6 shadow-xl border flex flex-col gap-4 max-h-[85vh] overflow-y-auto ${
              isDark ? 'bg-[#0c141d] text-slate-100 border-teal-900/40' : 'bg-white text-slate-900 border-slate-200'
            }`}>
              <div className="flex items-center justify-between pb-3 border-b border-slate-800 shrink-0">
                <div className="flex items-center gap-2.5">
                  <div className="w-9 h-9 rounded-xl bg-teal-500/15 text-teal-400 flex items-center justify-center">
                    <span className="material-symbols-outlined text-[20px]">notifications_active</span>
                  </div>
                  <h3 className="text-base font-bold">إعدادات التنبيه والمراحل</h3>
                </div>
                <button
                  type="button"
                  onClick={closeModal}
                  className="w-8 h-8 rounded-full bg-slate-800/60 hover:bg-slate-700 flex items-center justify-center text-slate-400 hover:text-white transition-colors cursor-pointer"
                >
                  <span className="material-symbols-outlined text-[18px]">close</span>
                </button>
              </div>

              <div className="flex flex-col gap-4 text-start">
                {/* Basic Customization */}
                <div className={`p-3.5 rounded-xl border space-y-3 ${
                  isDark ? 'bg-[#121c26] border-slate-800/80' : 'bg-slate-50 border-slate-200'
                }`}>
                  <div className="font-bold text-xs text-teal-400 flex items-center gap-2">
                    <span className="material-symbols-outlined text-[18px]">tune</span>
                    <span>التخصيص الأساسي</span>
                  </div>

                  <div className="flex items-center justify-between">
                    <div className="flex flex-col text-right">
                      <span className="font-bold text-xs">صوت التنبيه</span>
                      <span className="text-[11px] text-slate-400">نغمة الطوارئ الافتراضية</span>
                    </div>
                    <button
                      type="button"
                      onClick={() => onUpdateSettings({ ringtone: settings.ringtone === 'pulse_alarm' ? 'urgent_beep' : 'pulse_alarm' })}
                      className="px-2.5 py-1.5 rounded-lg bg-teal-500/15 border border-teal-500/30 text-teal-300 text-xs font-bold flex items-center gap-1.5 hover:bg-teal-500/25 transition-all cursor-pointer active:scale-95"
                    >
                      <span className="material-symbols-outlined text-[16px]">music_note</span>
                      <span>اختيار صوت التنبيه</span>
                    </button>
                  </div>

                  <div className="flex items-center justify-between pt-2 border-t border-slate-800">
                    <span className="text-xs font-bold text-teal-300 bg-[#1b2735] px-2 py-0.5 rounded-md border border-slate-700 font-mono">
                      10:00 AM
                    </span>
                    <span className="text-xs font-medium text-slate-300">وقت التنبيه الافتراضي</span>
                  </div>

                  <div className="flex items-center justify-between">
                    <span className="material-symbols-outlined text-teal-400 text-[22px]">toggle_on</span>
                    <span className="text-xs font-medium text-slate-300">الاهتزاز</span>
                  </div>

                  <div className="flex items-center justify-between">
                    <span className="material-symbols-outlined text-teal-400 text-[22px]">check_box</span>
                    <span className="text-xs font-medium text-slate-300">إيقاف تلقائي بعد 5 دقائق</span>
                  </div>

                  <div className="flex items-center justify-between">
                    <span className="material-symbols-outlined text-teal-400 text-[22px]">check_box</span>
                    <span className="text-xs font-medium text-slate-300">الغفوة (ساعة واحدة)</span>
                  </div>

                  <div className="flex items-center justify-between">
                    <span className="material-symbols-outlined text-teal-400 text-[22px]">toggle_on</span>
                    <span className="text-xs font-medium text-slate-300">تنبيه الأولوية القصوى فوق شاشة القفل</span>
                  </div>
                </div>

                {/* Grace Periods */}
                <div className={`p-3.5 rounded-xl border space-y-2.5 ${
                  isDark ? 'bg-[#121c26] border-slate-800/80' : 'bg-slate-50 border-slate-200'
                }`}>
                  <div className="font-bold text-xs text-teal-400 flex items-center gap-2">
                    <span className="material-symbols-outlined text-[18px]">event_upcoming</span>
                    <span>مهلة تنبيه</span>
                  </div>

                  <div className={`p-2.5 rounded-lg border flex items-start gap-2.5 text-right ${
                    isDark ? 'bg-[#162230] border-slate-800' : 'bg-white border-slate-200'
                  }`}>
                    <span className="material-symbols-outlined text-teal-400 text-[18px] shrink-0 mt-0.5">looks_one</span>
                    <div>
                      <div className="font-bold text-xs">تنبيه مسبق قبل بيوم</div>
                      <div className="text-[11px] text-slate-400">إشعار هادئ للتذكير بموعد البصمة غداً</div>
                    </div>
                  </div>

                  <div className="p-2.5 rounded-lg bg-red-950/20 border border-red-800/30 flex items-start gap-2.5 text-right">
                    <span className="material-symbols-outlined text-red-500 text-[18px] shrink-0 mt-0.5">warning</span>
                    <div>
                      <div className="font-bold text-xs text-red-400">يوم البصمة: إنذار صارم</div>
                      <div className="text-[11px] text-slate-300">تنبيه متكرر بأعلى نغمة حتى يتم التوثيق</div>
                    </div>
                  </div>

                  <div className={`p-2.5 rounded-lg border flex items-start gap-2.5 text-right ${
                    isDark ? 'bg-[#162230] border-slate-800' : 'bg-white border-slate-200'
                  }`}>
                    <span className="material-symbols-outlined text-teal-400 text-[18px] shrink-0 mt-0.5">looks_two</span>
                    <div>
                      <div className="font-bold text-xs">تذكير اليوم الثاني</div>
                      <div className="text-[11px] text-slate-400">تنبيه عاجل لدخول فترة المهلة الرسمية</div>
                    </div>
                  </div>

                  <div className={`p-2.5 rounded-lg border flex items-start gap-2.5 text-right ${
                    isDark ? 'bg-[#162230] border-slate-800' : 'bg-white border-slate-200'
                  }`}>
                    <span className="material-symbols-outlined text-teal-400 text-[18px] shrink-0 mt-0.5">priority_high</span>
                    <div>
                      <div className="font-bold text-xs">الفرصة الأخيرة في اليوم الثالث</div>
                      <div className="text-[11px] text-slate-400">إنذار نهائي قبل انتهاء صلاحية البصمة الدورية</div>
                    </div>
                  </div>
                </div>
              </div>

              <button
                type="button"
                onClick={closeModal}
                className="w-full py-3 bg-teal-500/20 text-teal-300 border border-teal-500/30 hover:bg-teal-500/30 font-bold rounded-xl shadow-sm text-center transition-all cursor-pointer active:scale-95"
              >
                حفظ التغييرات
              </button>
            </div>
          </div>
        )}

        {/* Modal 3: Permissions (الأذونات وتفادي الإغلاق) matching exact HTML */}
        {activeModal === 'perms' && (
          <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4 transition-all duration-300">
            <div className={`w-full max-w-md rounded-t-[28px] sm:rounded-2xl p-6 shadow-xl border flex flex-col gap-4 animate-in fade-in slide-in-from-bottom duration-200 ${
              isDark ? 'bg-[#0c141d] text-slate-100 border-teal-900/40' : 'bg-white text-slate-900 border-slate-200'
            }`}>
              <div className="flex items-center justify-between pb-3 border-b border-slate-800 shrink-0">
                <div className="flex items-center gap-2.5">
                  <div className="w-9 h-9 rounded-xl bg-teal-500/15 border border-teal-500/30 text-teal-400 flex items-center justify-center shadow-sm">
                    <span className="material-symbols-outlined text-[20px]">verified_user</span>
                  </div>
                  <h3 className="text-base font-bold tracking-tight">الأذونات وتفادي الإغلاق</h3>
                </div>
                <button
                  type="button"
                  onClick={closeModal}
                  className="w-9 h-9 rounded-full bg-slate-800/80 hover:bg-slate-700 flex items-center justify-center text-slate-400 hover:text-white transition-colors cursor-pointer active:scale-95"
                >
                  <span className="material-symbols-outlined text-[18px]">close</span>
                </button>
              </div>

              <div className="flex flex-col gap-3 text-start">
                {/* 1. Display over apps */}
                <div className={`p-4 rounded-2xl border flex items-center justify-between gap-3 ${
                  isDark ? 'bg-[#121c26] border-teal-900/40' : 'bg-slate-50 border-slate-200'
                }`}>
                  <button
                    type="button"
                    onClick={() => onUpdateSettings({ displayOverAppsGranted: !settings.displayOverAppsGranted })}
                    className="flex items-center gap-1 px-3 py-1.5 rounded-xl bg-teal-500/15 border border-teal-500/30 text-teal-300 text-xs font-bold hover:bg-teal-500/25 transition-all cursor-pointer shrink-0 active:scale-95"
                  >
                    <span>{settings.displayOverAppsGranted ? 'مُفعّل' : 'تفعيل'}</span>
                    <span className="material-symbols-outlined text-[16px] rtl:-scale-x-100">arrow_forward</span>
                  </button>
                  <div className="flex flex-col min-w-0 text-right">
                    <span className="font-bold text-sm">الظهور فوق التطبيقات</span>
                    <span className="text-xs text-slate-400 leading-relaxed mt-0.5">لعرض نافذة الإنذار الفورية حتى أثناء القفل</span>
                  </div>
                </div>

                {/* 2. Important Notifications */}
                <div className={`p-4 rounded-2xl border flex items-center justify-between gap-3 ${
                  isDark ? 'bg-[#121c26] border-teal-900/40' : 'bg-slate-50 border-slate-200'
                }`}>
                  <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-teal-950/80 border border-teal-500/30 text-teal-300 text-xs font-bold shrink-0">
                    <span className="material-symbols-outlined text-[16px] text-teal-400">check_circle</span>
                    <span>مُفعّل</span>
                  </div>
                  <div className="flex flex-col min-w-0 text-right">
                    <span className="font-bold text-sm">الإشعارات المهمة</span>
                    <span className="text-xs text-slate-400 leading-relaxed mt-0.5">تخطي نمط عدم الإزعاج أثناء مهلة البصمة</span>
                  </div>
                </div>

                {/* 3. Battery Saver Exemption */}
                <div className={`p-4 rounded-2xl border flex items-center justify-between gap-3 ${
                  isDark ? 'bg-[#121c26] border-teal-900/40' : 'bg-slate-50 border-slate-200'
                }`}>
                  <button
                    type="button"
                    onClick={() => onUpdateSettings({ batteryExemptionGranted: !settings.batteryExemptionGranted })}
                    className="flex items-center gap-1 px-3 py-1.5 rounded-xl bg-teal-500/15 border border-teal-500/30 text-teal-300 text-xs font-bold hover:bg-teal-500/25 transition-all cursor-pointer shrink-0 active:scale-95"
                  >
                    <span>{settings.batteryExemptionGranted ? 'مُفعّل' : 'تفعيل'}</span>
                    <span className="material-symbols-outlined text-[16px] rtl:-scale-x-100">arrow_forward</span>
                  </button>
                  <div className="flex flex-col min-w-0 text-right">
                    <span className="font-bold text-sm">استثناء توفير البطارية</span>
                    <span className="text-xs text-slate-400 leading-relaxed mt-0.5">منع نظام التشغيل من قتل تطبيق التنبيه</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Modal 4: About App (حول التطبيق) matching exact HTML */}
        {activeModal === 'about-app' && (
          <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4 transition-all duration-300">
            <div className={`w-full max-w-md rounded-t-[28px] sm:rounded-2xl p-6 shadow-xl border flex flex-col gap-4 text-start animate-in fade-in slide-in-from-bottom duration-200 ${
              isDark ? 'bg-[#0c141d] text-slate-100 border-teal-900/40' : 'bg-white text-slate-900 border-slate-200'
            }`}>
              <div className="flex items-center justify-between pb-3 border-b border-slate-800 shrink-0">
                <div className="flex items-center gap-2.5">
                  <div className="w-9 h-9 rounded-xl bg-teal-500/15 border border-teal-500/30 text-teal-300 flex items-center justify-center">
                    <span className="material-symbols-outlined text-[20px]">info</span>
                  </div>
                  <h3 className="text-base font-bold">حول التطبيق</h3>
                </div>
                <button
                  type="button"
                  onClick={closeModal}
                  className="w-8 h-8 rounded-full bg-[#1b2735] flex items-center justify-center text-slate-400 hover:text-white transition-colors cursor-pointer active:scale-95"
                >
                  <span className="material-symbols-outlined text-[18px]">close</span>
                </button>
              </div>

              <div className="flex flex-col items-center text-center py-2">
                <div className="w-16 h-16 rounded-2xl bg-teal-500/15 border border-teal-500/30 text-teal-400 flex items-center justify-center mb-2 shadow-[0_0_12px_rgba(20,184,166,0.15)]">
                  <span className="material-symbols-outlined text-[36px]">fingerprint</span>
                </div>
                <h4 className="text-base font-bold">نظام متابعة البصمة الدورية</h4>
                <span className="text-xs text-teal-300 bg-teal-950/80 border border-teal-500/30 px-3 py-0.5 rounded-full mt-1">
                  الإصدار v2.4.0 (Build 142)
                </span>
              </div>

              <div className={`p-4 rounded-xl border text-xs leading-relaxed text-center ${
                isDark ? 'bg-[#121c26] border-slate-800/80 text-slate-300' : 'bg-slate-50 border-slate-200 text-slate-700'
              }`}>
                تطبيق يساعدك على عدم تفويت الالتزام بالبصمات والتواقيع الدورية المفروضة عليك، مع إدارة المهل القانونية والتنبيهات المسبقة بدقة متناهية.
              </div>

              <button
                type="button"
                onClick={closeModal}
                className="w-full py-3 bg-[#162230] hover:bg-[#1b2735] text-slate-200 border border-slate-700 font-bold rounded-xl shadow-sm text-center transition-all cursor-pointer active:scale-95"
              >
                إغلاق
              </button>
            </div>
          </div>
        )}

        {/* Modal 5: About Developer (حول المطور) matching exact HTML */}
        {activeModal === 'about-dev' && (
          <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4 transition-all duration-300">
            <div className={`w-full max-w-md rounded-t-[28px] sm:rounded-2xl p-6 shadow-xl border flex flex-col gap-4 text-start animate-in fade-in slide-in-from-bottom duration-200 ${
              isDark ? 'bg-[#0c141d] text-slate-100 border-teal-900/40' : 'bg-white text-slate-900 border-slate-200'
            }`}>
              <div className="flex items-center justify-between pb-3 border-b border-slate-800 shrink-0">
                <div className="flex items-center gap-2.5">
                  <div className="w-9 h-9 rounded-xl bg-teal-500/15 border border-teal-500/30 text-teal-300 flex items-center justify-center font-bold text-[18px]">
                    Y
                  </div>
                  <h3 className="text-base font-bold">حول المطور</h3>
                </div>
                <button
                  type="button"
                  onClick={closeModal}
                  className="w-8 h-8 rounded-full bg-[#1b2735] flex items-center justify-center text-slate-400 hover:text-white transition-colors cursor-pointer active:scale-95"
                >
                  <span className="material-symbols-outlined text-[18px]">close</span>
                </button>
              </div>

              <div className="flex flex-col items-center text-center py-2">
                <div className="w-16 h-16 rounded-full bg-teal-500/15 border-2 border-teal-500/40 text-teal-300 flex items-center justify-center mb-2 shadow-[0_0_12px_rgba(20,184,166,0.25)] select-none">
                  <span className="font-bold text-[28px] tracking-tight">Y</span>
                </div>
                <h4 className="text-base font-bold">تم البرمجة والتطوير من قبل ياسر أبو علاء</h4>
                <p className="text-xs text-slate-400 mt-1">مختص ببرمجة التطبيقات والمواقع الإلكترونية</p>
              </div>

              <div className="flex flex-col gap-2.5 pt-1 text-right">
                {/* WhatsApp */}
                <a
                  className={`w-full p-3.5 rounded-xl border flex items-center justify-between transition-all cursor-pointer group active:scale-95 ${
                    isDark ? 'bg-[#121c26] hover:bg-[#162230] border-teal-900/40' : 'bg-slate-50 hover:bg-slate-100 border-slate-200'
                  }`}
                  href="https://wa.me/"
                  target="_blank"
                  rel="noreferrer"
                >
                  <div className="flex items-center gap-3.5 min-w-0">
                    <div className="w-11 h-11 rounded-xl bg-[#25D366]/15 border border-[#25D366]/30 flex items-center justify-center shrink-0">
                      <svg className="w-5 h-5 shrink-0 fill-[#25D366]" viewBox="0 0 24 24">
                        <path d="M.057 24l1.687-6.163c-1.041-1.804-1.588-3.849-1.587-5.946.003-6.556 5.338-11.891 11.893-11.891 3.181.001 6.167 1.24 8.413 3.488 2.245 2.248 3.481 5.236 3.48 8.414-.003 6.557-5.338 11.892-11.893 11.892-1.99-.001-3.951-.5-5.688-1.448l-6.305 1.654zm6.597-3.807c1.676.995 3.276 1.591 5.392 1.592 5.448 0 9.886-4.434 9.889-9.885.002-5.462-4.415-9.89-9.881-9.892-5.452 0-9.887 4.434-9.889 9.884-.001 2.225.651 3.891 1.746 5.634l-.999 3.648 3.742-.981zm11.387-5.464c-.074-.124-.272-.198-.57-.347-.297-.149-1.758-.868-2.031-.967-.272-.099-.47-.149-.669.149-.198.297-.768.967-.941 1.165-.173.198-.347.223-.644.074-.297-.149-1.255-.462-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.297-.347.446-.521.151-.172.2-.296.3-.495.099-.198.05-.372-.025-.521-.075-.148-.669-1.611-.916-2.206-.242-.579-.487-.501-.669-.51l-.57-.01c-.198 0-.52.074-.792.372s-1.04 1.016-1.04 2.479 1.065 2.876 1.213 3.074c.149.198 2.095 3.2 5.076 4.487.709.306 1.263.489 1.694.626.712.226 1.36.194 1.872.118.571-.085 1.758-.719 2.006-1.413.248-.695.248-1.29.173-1.414z"></path>
                      </svg>
                    </div>
                    <div className="flex flex-col min-w-0">
                      <span className="font-bold text-sm">واتساب (WhatsApp)</span>
                      <span className="text-xs text-slate-400 truncate">مراسلة مباشرة وسريعة</span>
                    </div>
                  </div>
                  <div className="flex items-center text-slate-400 group-hover:text-white transition-colors">
                    <span className="material-symbols-outlined text-[20px]">chevron_left</span>
                  </div>
                </a>

                {/* Telegram */}
                <a
                  className={`w-full p-3.5 rounded-xl border flex items-center justify-between transition-all cursor-pointer group active:scale-95 ${
                    isDark ? 'bg-[#121c26] hover:bg-[#162230] border-teal-900/40' : 'bg-slate-50 hover:bg-slate-100 border-slate-200'
                  }`}
                  href="https://t.me/"
                  target="_blank"
                  rel="noreferrer"
                >
                  <div className="flex items-center gap-3.5 min-w-0">
                    <div className="w-11 h-11 rounded-xl bg-[#0088cc]/15 border border-[#0088cc]/30 flex items-center justify-center shrink-0">
                      <svg className="w-5 h-5 shrink-0 fill-[#0088cc]" viewBox="0 0 24 24">
                        <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12 12-5.373 12-12S18.627 0 12 0zm5.894 8.221l-1.97 9.28c-.145.658-.537.818-1.084.508l-3-2.21-1.446 1.394c-.16.16-.295.295-.605.295l.213-3.053 5.56-5.023c.242-.213-.054-.333-.373-.121l-6.871 4.326-2.962-.924c-.643-.204-.657-.643.136-.953l11.57-4.461c.537-.194 1.006.131.832.942z"></path>
                      </svg>
                    </div>
                    <div className="flex flex-col min-w-0">
                      <span className="font-bold text-sm">تلجرام (Telegram)</span>
                      <span className="text-xs text-slate-400 truncate">محادثة وقناة التحديثات</span>
                    </div>
                  </div>
                  <div className="flex items-center text-slate-400 group-hover:text-white transition-colors">
                    <span className="material-symbols-outlined text-[20px]">chevron_left</span>
                  </div>
                </a>

                {/* Facebook */}
                <a
                  className={`w-full p-3.5 rounded-xl border flex items-center justify-between transition-all cursor-pointer group active:scale-95 ${
                    isDark ? 'bg-[#121c26] hover:bg-[#162230] border-teal-900/40' : 'bg-slate-50 hover:bg-slate-100 border-slate-200'
                  }`}
                  href="https://facebook.com/"
                  target="_blank"
                  rel="noreferrer"
                >
                  <div className="flex items-center gap-3.5 min-w-0">
                    <div className="w-11 h-11 rounded-xl bg-[#1877F2]/15 border border-[#1877F2]/30 flex items-center justify-center shrink-0">
                      <svg className="w-5 h-5 shrink-0 fill-[#1877F2]" viewBox="0 0 24 24">
                        <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"></path>
                      </svg>
                    </div>
                    <div className="flex flex-col min-w-0">
                      <span className="font-bold text-sm">فيسبوك (Facebook)</span>
                      <span className="text-xs text-slate-400 truncate">الصفحة والمتابعة الرسمية</span>
                    </div>
                  </div>
                  <div className="flex items-center text-slate-400 group-hover:text-white transition-colors">
                    <span className="material-symbols-outlined text-[20px]">chevron_left</span>
                  </div>
                </a>
              </div>
            </div>
          </div>
        )}

        {/* Modal 6: Disclaimer (إخلاء المسؤولية) matching exact HTML */}
        {activeModal === 'disclaimer' && (
          <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4 transition-all duration-300">
            <div className={`w-full max-w-md rounded-t-[28px] sm:rounded-2xl p-6 shadow-xl border flex flex-col gap-4 text-start animate-in fade-in slide-in-from-bottom duration-200 ${
              isDark ? 'bg-[#0c141d] text-slate-100 border-teal-900/40' : 'bg-white text-slate-900 border-slate-200'
            }`}>
              <div className="flex items-center justify-between pb-3 border-b border-slate-800 shrink-0">
                <div className="flex items-center gap-2.5">
                  <div className="w-9 h-9 rounded-xl bg-teal-500/15 text-teal-400 flex items-center justify-center">
                    <span className="material-symbols-outlined text-[20px]">gavel</span>
                  </div>
                  <h3 className="text-base font-bold">إخلاء المسؤولية</h3>
                </div>
                <button
                  type="button"
                  onClick={closeModal}
                  className="w-8 h-8 rounded-full bg-[#1b2735] flex items-center justify-center text-slate-400 hover:text-white transition-colors cursor-pointer active:scale-95"
                >
                  <span className="material-symbols-outlined text-[18px]">close</span>
                </button>
              </div>

              <div className={`p-4 rounded-xl border text-xs leading-relaxed text-right ${
                isDark ? 'bg-[#121c26] border-slate-800/80 text-slate-200' : 'bg-slate-50 border-slate-200 text-slate-700'
              }`}>
                هذا التطبيق هو أداة تنظيم ومساعد تذكير شخصي مستقل غير رسمي، ولا يمثل أي جهة حكومية أو دائرة رسمية. يُرجى التحقق دائمًا من الإشعارات المباشرة لجهات الاختصاص.
              </div>

              <button
                type="button"
                onClick={closeModal}
                className="w-full py-3 bg-teal-500/20 text-teal-300 border border-teal-500/30 hover:bg-teal-500/30 font-bold rounded-xl shadow-sm text-center transition-all cursor-pointer active:scale-95"
              >
                أوافق وأتفهم
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
