import React from 'react';
import { ThemeMode } from '../types';
import { X, HelpCircle, CheckCircle2 } from 'lucide-react';

interface InstructionsModalProps {
  isOpen: boolean;
  onClose: () => void;
  theme: ThemeMode;
}

export const InstructionsModal: React.FC<InstructionsModalProps> = ({
  isOpen,
  onClose,
  theme,
}) => {
  if (!isOpen) return null;
  const isDark = theme === 'dark';

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-in fade-in duration-200">
      <div
        className={`w-full max-w-sm rounded-2xl p-5 shadow-2xl transition-all border ${
          isDark
            ? 'bg-[#0a1926] border-slate-700 text-white'
            : 'bg-white border-slate-100 text-slate-900'
        }`}
      >
        {/* Header */}
        <div className="flex items-center justify-between mb-4">
          <button
            onClick={onClose}
            className={`p-1.5 rounded-full transition-colors ${
              isDark ? 'hover:bg-slate-800 text-slate-400' : 'hover:bg-slate-100 text-slate-500'
            }`}
          >
            <X className="w-5 h-5" />
          </button>

          <div className="flex items-center gap-2">
            <h3 className="text-base font-bold">تعليمات الإعداد</h3>
            <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
              isDark ? 'bg-teal-900/60 text-teal-400' : 'bg-teal-50 text-teal-700'
            }`}>
              <HelpCircle className="w-5 h-5" />
            </div>
          </div>
        </div>

        {/* Intro */}
        <p className={`text-sm leading-relaxed mb-4 text-right ${
          isDark ? 'text-slate-300' : 'text-slate-600'
        }`}>
          قم بتعيين تاريخ أول بصمة أو بصمتك القادمة، وحدد مدة البصمة، واختر ساعة بدء التنبيه.
        </p>

        {/* Bullet List Container */}
        <div className={`rounded-xl p-4 mb-5 text-right space-y-2.5 text-xs font-medium ${
          isDark ? 'bg-[#0f2537] text-slate-200 border border-slate-800' : 'bg-slate-50 text-slate-700 border border-slate-100'
        }`}>
          <div className="flex items-start gap-2 justify-start">
            <span className="w-2 h-2 rounded-full bg-teal-500 mt-1.5 shrink-0" />
            <span>حدد تاريخ بدء اليوم الأول المعتمد في تطبيق Gocbil</span>
          </div>
          <div className="flex items-start gap-2 justify-start">
            <span className="w-2 h-2 rounded-full bg-teal-500 mt-1.5 shrink-0" />
            <span>اختر وقت التنبيه</span>
          </div>
          <div className="flex items-start gap-2 justify-start">
            <span className="w-2 h-2 rounded-full bg-teal-500 mt-1.5 shrink-0" />
            <span>اختر مدة البصمة (28 يوماً أو أسبوعي)</span>
          </div>
          <div className="flex items-start gap-2 justify-start">
            <span className="w-2 h-2 rounded-full bg-teal-500 mt-1.5 shrink-0" />
            <span>اختر صوت التنبيه المناسب والاهتزاز</span>
          </div>
          <div className="flex items-start gap-2 justify-start">
            <span className="w-2 h-2 rounded-full bg-teal-500 mt-1.5 shrink-0" />
            <span>قم بالحفظ ليقوم النظام بإنشاء جميع البصمات القادمة تلقائياً</span>
          </div>
        </div>

        {/* CTA */}
        <button
          onClick={onClose}
          className="w-full py-3 px-4 rounded-xl font-bold text-sm bg-[#006a61] hover:bg-[#00574f] text-white shadow-md active:scale-98 transition-all flex items-center justify-center gap-2 cursor-pointer"
        >
          <CheckCircle2 className="w-4 h-4" />
          <span>فهمت ذلك</span>
        </button>
      </div>
    </div>
  );
};
