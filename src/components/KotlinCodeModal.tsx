import React, { useState } from 'react';
import { KOTLIN_PROJECT_FILES, KotlinFile } from '../data/kotlinCode';
import { ThemeMode } from '../types';
import { X, Copy, Check, Download, CodeXml, FileCode, Smartphone } from 'lucide-react';

interface KotlinCodeModalProps {
  isOpen: boolean;
  onClose: () => void;
  theme: ThemeMode;
}

export const KotlinCodeModal: React.FC<KotlinCodeModalProps> = ({
  isOpen,
  onClose,
  theme,
}) => {
  const [selectedFile, setSelectedFile] = useState<KotlinFile>(KOTLIN_PROJECT_FILES[0]);
  const [copied, setCopied] = useState(false);

  if (!isOpen) return null;
  const isDark = theme === 'dark';

  const handleCopy = () => {
    navigator.clipboard.writeText(selectedFile.content);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleDownload = () => {
    const blob = new Blob([selectedFile.content], { type: 'text/plain;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = selectedFile.name;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-2 sm:p-4 bg-black/70 backdrop-blur-md animate-in fade-in duration-200">
      <div
        className={`w-full max-w-4xl max-h-[92vh] flex flex-col rounded-3xl shadow-2xl overflow-hidden border ${
          isDark
            ? 'bg-[#05111d] border-slate-700 text-white'
            : 'bg-white border-slate-200 text-slate-900'
        }`}
      >
        {/* Modal Top Bar */}
        <div className={`p-4 border-b flex items-center justify-between ${
          isDark ? 'border-slate-800 bg-[#081b2e]' : 'border-slate-200 bg-slate-50'
        }`}>
          <button
            onClick={onClose}
            className={`p-2 rounded-full transition-colors cursor-pointer ${
              isDark ? 'hover:bg-slate-800 text-slate-400' : 'hover:bg-slate-200 text-slate-600'
            }`}
          >
            <X className="w-5 h-5" />
          </button>

          <div className="flex items-center gap-3">
            <div className="text-right">
              <h3 className="font-bold text-sm sm:text-base flex items-center gap-2 justify-end">
                <span>مشروع تطبيق Kotlin لأندرويد (Jetpack Compose)</span>
                <Smartphone className="w-4 h-4 text-teal-500" />
              </h3>
              <p className="text-[11px] text-slate-400">
                كود كامل أصلي لتشغيل المنبه الفعلي بدقة على أجهزة Android
              </p>
            </div>
            <div className="w-9 h-9 rounded-xl bg-teal-500/10 text-teal-500 flex items-center justify-center">
              <CodeXml className="w-5 h-5" />
            </div>
          </div>
        </div>

        {/* File Tabs Strip */}
        <div className={`px-3 py-2 border-b flex items-center gap-1.5 overflow-x-auto no-scrollbar ${
          isDark ? 'bg-[#030a12] border-slate-800' : 'bg-slate-100 border-slate-200'
        }`}>
          {KOTLIN_PROJECT_FILES.map((file) => {
            const isSelected = selectedFile.name === file.name;
            return (
              <button
                key={file.name}
                onClick={() => setSelectedFile(file)}
                className={`px-3 py-1.5 rounded-xl text-xs font-mono font-medium flex items-center gap-1.5 shrink-0 transition-all cursor-pointer ${
                  isSelected
                    ? isDark
                      ? 'bg-teal-950 text-teal-300 border border-teal-700/60'
                      : 'bg-white text-teal-900 border border-teal-300 shadow-sm'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/40'
                }`}
              >
                <FileCode className="w-3.5 h-3.5" />
                <span>{file.name}</span>
              </button>
            );
          })}
        </div>

        {/* File Info & Action Buttons */}
        <div className={`px-4 py-2.5 flex items-center justify-between border-b text-xs ${
          isDark ? 'bg-[#091e32] border-slate-800 text-slate-300' : 'bg-slate-50 border-slate-200 text-slate-600'
        }`}>
          <div className="flex items-center gap-2">
            <button
              onClick={handleCopy}
              className={`px-3 py-1.5 rounded-xl font-bold flex items-center gap-1.5 transition-all cursor-pointer ${
                copied
                  ? 'bg-emerald-600 text-white'
                  : isDark
                  ? 'bg-slate-800 hover:bg-slate-700 text-white'
                  : 'bg-white hover:bg-slate-100 border text-slate-800'
              }`}
            >
              {copied ? <Check className="w-3.5 h-3.5" /> : <Copy className="w-3.5 h-3.5" />}
              <span>{copied ? 'تم النسخ!' : 'نسخ الكود'}</span>
            </button>

            <button
              onClick={handleDownload}
              className={`px-3 py-1.5 rounded-xl font-bold flex items-center gap-1.5 transition-all cursor-pointer ${
                isDark
                  ? 'bg-slate-800 hover:bg-slate-700 text-teal-300'
                  : 'bg-white hover:bg-slate-100 border text-teal-800'
              }`}
            >
              <Download className="w-3.5 h-3.5" />
              <span>تحميل الملف</span>
            </button>
          </div>

          <div className="text-right hidden sm:block">
            <span className="font-semibold">{selectedFile.description}</span>
          </div>
        </div>

        {/* Code Content Display */}
        <div className="flex-1 overflow-auto p-4 bg-[#02070e] text-slate-200 font-mono text-xs leading-relaxed select-text direction-ltr text-left">
          <pre className="whitespace-pre">
            <code>{selectedFile.content}</code>
          </pre>
        </div>

        {/* Footer Notes */}
        <div className={`p-3 border-t text-[11px] text-right flex items-center justify-between ${
          isDark ? 'bg-[#081b2e] border-slate-800 text-slate-400' : 'bg-slate-100 border-slate-200 text-slate-600'
        }`}>
          <span>تم بناء الكود ليتوافق مع أحدث إصدارات Android 14 و Android 15 مع صلاحية ExactAlarm.</span>
          <span className="font-mono text-teal-400 font-bold">{selectedFile.path}</span>
        </div>
      </div>
    </div>
  );
};
