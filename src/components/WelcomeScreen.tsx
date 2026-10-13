import React from 'react';
import { ThemeMode } from '../types';
import { ArrowLeft, CheckCircle2 } from 'lucide-react';

interface WelcomeScreenProps {
  onStart: () => void;
  theme: ThemeMode;
}

export const WelcomeScreen: React.FC<WelcomeScreenProps> = ({ onStart, theme }) => {
  const isDark = theme === 'dark';

  const logoUrl =
    'https://lh3.googleusercontent.com/aida-public/AB6AXuDBxpJ5b_wCRDJ-s89LK7DZhBwEIvAkP3s36JkimxQ_j2vmgxzQZ-YaqqWSx7at-1e58mZZOGZQcNE74X4OuY_leq6GX6sGP_fcPUeEfiE_BA0Exn82FQEzlPtboWYtTRQFzYF7lamZaevPWz65P5vHUa5F31NLFIOXqS83w4vMYmSG_xKo2WBoMLh1BW8qCzWR20i99m1CsT52_HjMwV8GioPK6juy4NHD-Kkn0PTfzGa6zdEiuyNRNw-5zshJwO2KUzo';

  return (
    <div className={`w-full min-h-screen px-4 pb-20 pt-4 flex flex-col transition-colors ${
      isDark ? 'bg-[#000f1d] text-white' : 'bg-[#f7f9fb] text-[#191c1e]'
    }`}>
      <div className="max-w-md mx-auto w-full flex flex-col">
        {/* Top Branding Section */}
        <section className="flex flex-col items-center text-center pt-2 pb-5">
          {/* Logo with Glow */}
          <div className="relative flex items-center justify-center mb-4">
            <div className="absolute -inset-2 bg-teal-400/30 rounded-full blur-xl transform scale-110" />
            <div className={`relative w-24 h-24 rounded-full p-1.5 shadow-md flex items-center justify-center overflow-hidden border ${
              isDark ? 'bg-[#0f2537] border-slate-700' : 'bg-white border-slate-200'
            }`}>
              <img
                src={logoUrl}
                alt="شعار منبه بصمة"
                className="w-full h-full object-cover rounded-full"
                onError={(e) => {
                  // Fallback if network blocked
                  (e.target as HTMLElement).style.display = 'none';
                }}
              />
              <div className="absolute inset-0 flex items-center justify-center pointer-events-none -z-10">
                <span className="material-symbols-outlined text-[48px] text-red-500">
                  fingerprint
                </span>
              </div>
            </div>
            <span className="absolute -bottom-1 -right-1 bg-[#000f1d] dark:bg-teal-700 text-teal-400 dark:text-white w-7 h-7 rounded-full shadow-sm flex items-center justify-center border-2 border-white dark:border-slate-800">
              <CheckCircle2 className="w-4 h-4 text-teal-400" />
            </span>
          </div>

          {/* Version Badge */}
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-teal-100 dark:bg-teal-950/80 text-teal-900 dark:text-teal-300 shadow-sm mb-3">
            <span className="text-xs font-semibold">الإصدار v1.2.0 الجديد</span>
          </div>

          {/* Headlines */}
          <h1 className="text-2xl font-bold tracking-tight mb-2">
            مرحبًا بك في منبّه بصمة
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 max-w-xs leading-relaxed">
            كل مواعيدك وتنبيهاتك في مكان واحد وبدقة متناهية لا تقبل الخطأ.
          </p>
        </section>

        {/* Feature Highlights Header */}
        <div className="flex items-center gap-2 mb-3 mt-1">
          <span className="w-1.5 h-4 bg-[#006a61] rounded-full" />
          <h2 className="text-base font-bold">أبرز الميزات والتحديثات</h2>
        </div>

        {/* Feature Cards Grid */}
        <div className="flex flex-col gap-2.5 mb-6">
          {/* Card 1: Critical Alarms */}
          <div className={`w-full rounded-2xl p-4 shadow-sm border transition-all ${
            isDark ? 'bg-[#071928] border-slate-800' : 'bg-white border-slate-100'
          }`}>
            <div className="flex items-start justify-between gap-2 mb-1.5">
              <div className="flex items-center gap-2.5 min-w-0">
                <div className="w-9 h-9 rounded-xl bg-red-100 dark:bg-red-950/70 text-red-600 dark:text-red-400 flex items-center justify-center shrink-0">
                  <span className="material-symbols-outlined text-[20px]" style={{ fontVariationSettings: "'FILL' 1" }}>
                    notifications_active
                  </span>
                </div>
                <h3 className="font-bold text-xs sm:text-sm truncate">تنبيهات فعلية لمواعيد البصمة</h3>
              </div>
              <span className="px-2 py-0.5 rounded-full bg-red-100 dark:bg-red-950 text-red-700 dark:text-red-300 text-[10px] font-bold shrink-0">
                أولوية قصوى
              </span>
            </div>
            <p className="text-[11px] sm:text-xs text-slate-500 dark:text-slate-400 pr-11 leading-relaxed">
              منبهات حقيقية تعمل بدقة حتى في وضع السكون وقفل الشاشة مع نغمات تنبيه مخصصة واهتزاز مستمر لضمان استيقاظك وتنبيهك فوراً.
            </p>
          </div>

          {/* Card 2: 28-day cadence */}
          <div className={`w-full rounded-2xl p-4 shadow-sm border transition-all ${
            isDark ? 'bg-[#071928] border-slate-800' : 'bg-white border-slate-100'
          }`}>
            <div className="flex items-start justify-between gap-2 mb-1.5">
              <div className="flex items-center gap-2.5 min-w-0">
                <div className="w-9 h-9 rounded-xl bg-teal-50 dark:bg-teal-950/70 text-[#006a61] dark:text-teal-300 flex items-center justify-center shrink-0">
                  <span className="material-symbols-outlined text-[20px]">
                    calendar_month
                  </span>
                </div>
                <h3 className="font-bold text-xs sm:text-sm truncate">تكرار ذكي كل ٢٨ يومًا أو أسبوعياً</h3>
              </div>
              <span className="px-2 py-0.5 rounded-full bg-teal-100 dark:bg-teal-900 text-teal-800 dark:text-teal-200 text-[10px] font-bold shrink-0">
                حصري
              </span>
            </div>
            <p className="text-[11px] sm:text-xs text-slate-500 dark:text-slate-400 pr-11 leading-relaxed">
              خوارزمية تكرار دقيقة كل 28 يوماً بالضبط لتطابق المهلة القانونية الرسمية لمواعيد الإقامة والتوثيق، بعيداً عن أخطاء التقويم الشهري المعتاد.
            </p>
          </div>

          {/* Card 3: 3-Day Window Tracking */}
          <div className={`w-full rounded-2xl p-4 shadow-sm border transition-all ${
            isDark ? 'bg-[#071928] border-slate-800' : 'bg-white border-slate-100'
          }`}>
            <div className="flex items-start justify-between gap-2 mb-1.5">
              <div className="flex items-center gap-2.5 min-w-0">
                <div className="w-9 h-9 rounded-xl bg-sky-100 dark:bg-sky-950/70 text-sky-700 dark:text-sky-300 flex items-center justify-center shrink-0">
                  <span className="material-symbols-outlined text-[20px]" style={{ fontVariationSettings: "'FILL' 1" }}>
                    view_timeline
                  </span>
                </div>
                <h3 className="font-bold text-xs sm:text-sm truncate">نافذة المتابعة لـ ٣ أيام متتالية</h3>
              </div>
              <span className="px-2 py-0.5 rounded-full bg-sky-100 dark:bg-sky-900 text-sky-800 dark:text-sky-200 text-[10px] font-bold shrink-0">
                نظام الـ ٣ أيام
              </span>
            </div>
            <p className="text-[11px] sm:text-xs text-slate-500 dark:text-slate-400 pr-11 leading-relaxed">
              تتبع شامل ومنهجي: اليوم الأول (الموعد الرئيسي)، اليوم الثاني (مهلة المتابعة والتذكير)، واليوم الثالث (الفرصة الأخيرة قبل انتهاء الصلاحية).
            </p>
          </div>

          {/* Card 4: Smart Snooze */}
          <div className={`w-full rounded-2xl p-4 shadow-sm border transition-all ${
            isDark ? 'bg-[#071928] border-slate-800' : 'bg-white border-slate-100'
          }`}>
            <div className="flex items-start justify-between gap-2 mb-1.5">
              <div className="flex items-center gap-2.5 min-w-0">
                <div className="w-9 h-9 rounded-xl bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 flex items-center justify-center shrink-0">
                  <span className="material-symbols-outlined text-[20px]">
                    snooze
                  </span>
                </div>
                <h3 className="font-bold text-xs sm:text-sm truncate">تذكير تلقائي بعد ساعة عند التأجيل</h3>
              </div>
            </div>
            <p className="text-[11px] sm:text-xs text-slate-500 dark:text-slate-400 pr-11 leading-relaxed">
              زر غفوة مرن يعيد جدولة الإشعار صوتياً بعد 60 دقيقة في حال كنت مشغولاً أثناء التنبيه الأول لمنع تراكم الغرامات أو تفويت البصمة.
            </p>
          </div>

          {/* Card 5: Boot Reschedule */}
          <div className={`w-full rounded-2xl p-4 shadow-sm border transition-all ${
            isDark ? 'bg-[#071928] border-slate-800' : 'bg-white border-slate-100'
          }`}>
            <div className="flex items-start justify-between gap-2 mb-1.5">
              <div className="flex items-center gap-2.5 min-w-0">
                <div className="w-9 h-9 rounded-xl bg-emerald-100 dark:bg-emerald-950/70 text-emerald-700 dark:text-emerald-300 flex items-center justify-center shrink-0">
                  <span className="material-symbols-outlined text-[20px]">
                    restart_alt
                  </span>
                </div>
                <h3 className="font-bold text-xs sm:text-sm truncate">استعادة التنبيهات</h3>
              </div>
              <span className="px-2 py-0.5 rounded-full bg-emerald-100 dark:bg-emerald-900 text-emerald-800 dark:text-emerald-200 text-[10px] font-bold shrink-0">
                موثوقية عالية
              </span>
            </div>
            <p className="text-[11px] sm:text-xs text-slate-500 dark:text-slate-400 pr-11 leading-relaxed">
              خدمة نظام مدمجة تقوم بمزامنة وإعادة جدولة كافة مواعيدك فوراً عند إعادة تشغيل الهاتف حتى دون حاجتك لفتح التطبيق يدوياً.
            </p>
          </div>

          {/* Card 6: Offline Mode */}
          <div className={`w-full rounded-2xl p-4 shadow-sm border transition-all ${
            isDark ? 'bg-[#071928] border-slate-800' : 'bg-white border-slate-100'
          }`}>
            <div className="flex items-start justify-between gap-2 mb-1.5">
              <div className="flex items-center gap-2.5 min-w-0">
                <div className="w-9 h-9 rounded-xl bg-slate-100 dark:bg-slate-800 text-slate-500 dark:text-slate-400 flex items-center justify-center shrink-0">
                  <span className="material-symbols-outlined text-[20px]">
                    wifi_off
                  </span>
                </div>
                <h3 className="font-bold text-xs sm:text-sm truncate">يعمل بالكامل بدون إنترنت</h3>
              </div>
              <span className="px-2 py-0.5 rounded-full bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 text-[10px] font-semibold shrink-0">
                خصوصية تامة
              </span>
            </div>
            <p className="text-[11px] sm:text-xs text-slate-500 dark:text-slate-400 pr-11 leading-relaxed">
              كافة سجلاتك ومعلوماتك مخزنة محلياً ومشفرة في ذاكرة جهازك فقط. لا خوادم خارجية، ولا استهلاك لباقة البيانات.
            </p>
          </div>
        </div>

        {/* CTA Button */}
        <section className="flex flex-col items-center w-full gap-2 mt-auto">
          <button
            onClick={onStart}
            className="w-full h-14 rounded-2xl bg-[#0f2537] hover:bg-[#15344d] text-white flex items-center justify-center gap-2 shadow-lg active:scale-98 transition-all cursor-pointer font-bold text-base"
          >
            <span>ابدأ الاستخدام الآن</span>
            <ArrowLeft className="w-5 h-5 rtl:scale-x-100" />
          </button>

          <p className="text-xs text-slate-500 dark:text-slate-400 text-center max-w-xs mt-1">
            يمكنك دائماً مراجعة هذه الميزات وتفاصيل التحديث من قسم <span className="font-bold text-[#006a61] dark:text-teal-400">الإعدادات</span> في أي وقت.
          </p>

          <div className="flex items-center justify-center gap-1.5 text-slate-400 dark:text-slate-500 py-1 text-[11px]">
            <span className="material-symbols-outlined text-[14px]">fingerprint</span>
            <span>منبّه بصمة v1.2.0 (Build 124) • تطبيق مستقل</span>
          </div>
        </section>
      </div>
    </div>
  );
};
