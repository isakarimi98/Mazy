import React, { useState } from 'react';
import { 
  ArrowRight, 
  FileDown, 
  Share2, 
  Printer, 
  Check, 
  Sparkles, 
  Copy, 
  Contrast, 
  Sun, 
  Image as ImageIcon,
  CheckCircle2,
  X
} from 'lucide-react';
import { DocumentItem, FilterType } from '../../types';
import { FILTER_OPTIONS } from '../../data/mockDocuments';

interface PreviewScreenViewProps {
  document: DocumentItem;
  onBack: () => void;
  onDone: () => void;
}

export const PreviewScreenView: React.FC<PreviewScreenViewProps> = ({
  document,
  onBack,
  onDone
}) => {
  const [activeFilter, setActiveFilter] = useState<FilterType>(document.activeFilter);
  const [showExportModal, setShowExportModal] = useState(false);
  const [pdfQuality, setPdfQuality] = useState<'high' | 'medium'>('high');
  const [paperSize, setPaperSize] = useState<'A4' | 'Letter'>('A4');
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const triggerToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 2500);
  };

  const getFilterIcon = (filterId: FilterType) => {
    switch (filterId) {
      case 'original': return <ImageIcon className="w-5 h-5" />;
      case 'photocopy': return <Copy className="w-5 h-5" />;
      case 'bw_office': return <Contrast className="w-5 h-5" />;
      case 'whiteboard': return <Sun className="w-5 h-5" />;
      case 'magic_color': return <Sparkles className="w-5 h-5" />;
    }
  };

  // Compute paper styling based on active filter
  const getPaperStyles = () => {
    switch (activeFilter) {
      case 'photocopy':
        return {
          bg: 'bg-white',
          textBg: 'bg-slate-900',
          stampColor: 'border-slate-900 text-slate-900',
          contrast: 'contrast-125'
        };
      case 'bw_office':
        return {
          bg: 'bg-white',
          textBg: 'bg-black',
          stampColor: 'border-black text-black',
          contrast: 'contrast-150'
        };
      case 'whiteboard':
        return {
          bg: 'bg-slate-50',
          textBg: 'bg-slate-800',
          stampColor: 'border-slate-700 text-slate-700',
          contrast: 'brightness-105'
        };
      case 'magic_color':
        return {
          bg: 'bg-[#fafafa]',
          textBg: 'bg-slate-900',
          stampColor: 'border-teal-700 text-teal-700',
          contrast: 'saturate-125'
        };
      case 'original':
      default:
        return {
          bg: 'bg-[#f4efe4]',
          textBg: 'bg-slate-700/70',
          stampColor: 'border-amber-900/60 text-amber-900/60',
          contrast: ''
        };
    }
  };

  const paperStyle = getPaperStyles();
  const currentOption = FILTER_OPTIONS.find(f => f.id === activeFilter) || FILTER_OPTIONS[1];

  return (
    <div className="flex flex-col h-full bg-slate-100 text-slate-800 relative select-none">
      {/* Toast Notification */}
      {toastMessage && (
        <div className="absolute top-16 left-1/2 -translate-x-1/2 bg-slate-900 text-white text-xs font-semibold px-4 py-2 rounded-full shadow-lg z-50 flex items-center gap-2 border border-slate-700 animate-in fade-in duration-150">
          <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          <span>{toastMessage}</span>
        </div>
      )}

      {/* Top Header */}
      <div className="flex items-center justify-between px-4 py-3 bg-white border-b border-slate-200 z-10">
        <div className="flex items-center gap-2">
          <button 
            onClick={onBack}
            className="p-1.5 rounded-full hover:bg-slate-100 text-slate-600"
          >
            <ArrowRight className="w-5 h-5" />
          </button>
          <div>
            <h2 className="font-bold text-sm text-slate-900 leading-tight">{document.title}</h2>
            <span className="text-[11px] text-slate-400">پیش‌نمایش چاپ و فیلتر</span>
          </div>
        </div>

        <button
          onClick={() => setShowExportModal(true)}
          className="flex items-center gap-1.5 bg-teal-50 hover:bg-teal-100 text-teal-800 border border-teal-200 px-3 py-1.5 rounded-xl text-xs font-bold transition-colors"
        >
          <FileDown className="w-4 h-4 text-teal-700" />
          <span>خروجی PDF</span>
        </button>
      </div>

      {/* Main Preview Document View */}
      <div className="flex-1 overflow-hidden p-5 flex items-center justify-center bg-slate-200/70">
        <div 
          className={`w-64 h-92 ${paperStyle.bg} rounded-lg shadow-xl p-5 border border-slate-300/80 flex flex-col justify-between transition-all duration-300`}
        >
          {/* Document Top Header */}
          <div className="flex justify-between items-center border-b border-slate-200 pb-2">
            <div className="flex items-center gap-1.5">
              <div className={`w-3.5 h-3.5 rounded-xs ${paperStyle.textBg}`} />
              <span className="text-[10px] font-bold text-slate-700">جمهوری اسلامی ایران</span>
            </div>
            <div className="text-[9px] text-slate-500 font-mono">
              شماره: ۱۴۰۳/س/۹۹
            </div>
          </div>

          {/* Document Body Lines */}
          <div className="space-y-2.5 my-auto">
            <div className={`w-2/5 h-2.5 ${paperStyle.textBg} rounded-xs`} />
            <div className={`w-full h-1.5 ${paperStyle.textBg} opacity-80 rounded-xs`} />
            <div className={`w-11/12 h-1.5 ${paperStyle.textBg} opacity-80 rounded-xs`} />
            <div className={`w-full h-1.5 ${paperStyle.textBg} opacity-80 rounded-xs`} />
            <div className={`w-4/5 h-1.5 ${paperStyle.textBg} opacity-80 rounded-xs`} />
            <div className={`w-full h-1.5 ${paperStyle.textBg} opacity-80 rounded-xs`} />
            <div className={`w-3/4 h-1.5 ${paperStyle.textBg} opacity-80 rounded-xs`} />
          </div>

          {/* Official Stamp Box */}
          <div className="flex justify-between items-end pt-3 border-t border-slate-200">
            <div className="text-[8px] text-slate-400">
              صفحه ۱ از {document.pageCount}
            </div>
            <div className={`px-2.5 py-1.5 rounded-md border-2 ${paperStyle.stampColor} text-center font-bold text-[9px] rotate-[-4deg]`}>
              ممهور و گواهی شد
            </div>
          </div>
        </div>
      </div>

      {/* Filter Selector Bottom Bar */}
      <div className="bg-white border-t border-slate-200/90 p-3.5 shadow-lg space-y-3 z-10">
        <div className="text-right">
          <span className="text-[11px] text-slate-400">فیلتر فعال: </span>
          <span className="text-xs font-bold text-teal-800">{currentOption.titleFa}</span>
          <span className="text-[10px] text-slate-500 mr-1.5">({currentOption.descFa})</span>
        </div>

        {/* 5 Filters Row */}
        <div className="flex items-center justify-between gap-1 overflow-x-auto pb-1">
          {FILTER_OPTIONS.map((filter) => {
            const isSelected = activeFilter === filter.id;
            return (
              <button
                key={filter.id}
                onClick={() => setActiveFilter(filter.id)}
                className={`flex flex-col items-center gap-1.5 p-2 rounded-xl transition-all min-w-[58px] ${
                  isSelected 
                    ? 'bg-teal-50 border border-teal-500 text-teal-800 font-bold scale-105' 
                    : 'text-slate-500 hover:bg-slate-100 font-medium'
                }`}
              >
                <div className={`w-9 h-9 rounded-lg flex items-center justify-center ${
                  isSelected ? 'bg-teal-700 text-white shadow-xs' : 'bg-slate-100 text-slate-600'
                }`}>
                  {getFilterIcon(filter.id)}
                </div>
                <span className="text-[10px] whitespace-nowrap">{filter.titleFa}</span>
              </button>
            );
          })}
        </div>

        <button
          onClick={onDone}
          className="w-full py-2.5 bg-teal-700 hover:bg-teal-800 text-white rounded-xl text-xs font-bold shadow-xs transition-colors"
        >
          ذخیره تغییرات سند
        </button>
      </div>

      {/* Export PDF Modal Sheet */}
      {showExportModal && (
        <div 
          className="absolute inset-0 bg-slate-900/60 backdrop-blur-xs z-30 flex flex-col justify-end transition-opacity"
          onClick={() => setShowExportModal(false)}
        >
          <div 
            className="bg-white rounded-t-3xl p-5 shadow-2xl space-y-4 animate-in slide-in-from-bottom duration-200"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="w-12 h-1 bg-slate-300 rounded-full mx-auto" />
            <div className="flex items-center justify-between">
              <div className="text-right">
                <h3 className="font-bold text-base text-slate-900">تنظیمات خروجی PDF</h3>
                <p className="text-xs text-slate-500">{document.title}</p>
              </div>
              <button 
                onClick={() => setShowExportModal(false)}
                className="p-1 rounded-full text-slate-400 hover:bg-slate-100"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Quality Picker */}
            <div>
              <label className="text-xs font-bold text-slate-700 block mb-1.5 text-right">کیفیت خروجی سند</label>
              <div className="grid grid-cols-2 gap-2">
                <button
                  onClick={() => setPdfQuality('high')}
                  className={`p-2.5 rounded-xl border text-right transition-colors ${
                    pdfQuality === 'high' 
                      ? 'border-teal-600 bg-teal-50/70 text-teal-900 font-bold' 
                      : 'border-slate-200 text-slate-600'
                  }`}
                >
                  <div className="text-xs font-bold">عالی (کیفیت اصلی چاپ)</div>
                  <div className="text-[10px] text-slate-500 mt-0.5">۳۰۰ DPI برای بایگانی اداری</div>
                </button>
                <button
                  onClick={() => setPdfQuality('medium')}
                  className={`p-2.5 rounded-xl border text-right transition-colors ${
                    pdfQuality === 'medium' 
                      ? 'border-teal-600 bg-teal-50/70 text-teal-900 font-bold' 
                      : 'border-slate-200 text-slate-600'
                  }`}
                >
                  <div className="text-xs font-bold">فشرده (ارسال سریع)</div>
                  <div className="text-[10px] text-slate-500 mt-0.5">۱۵۰ DPI مناسب پیام‌رسان</div>
                </button>
              </div>
            </div>

            {/* Paper Size Picker */}
            <div>
              <label className="text-xs font-bold text-slate-700 block mb-1.5 text-right">اندازه کاغذ</label>
              <div className="grid grid-cols-2 gap-2">
                <button
                  onClick={() => setPaperSize('A4')}
                  className={`py-2 px-3 rounded-xl border text-xs font-bold transition-colors ${
                    paperSize === 'A4' 
                      ? 'border-teal-600 bg-teal-50/70 text-teal-900' 
                      : 'border-slate-200 text-slate-600'
                  }`}
                >
                  کاغذ استاندار A4
                </button>
                <button
                  onClick={() => setPaperSize('Letter')}
                  className={`py-2 px-3 rounded-xl border text-xs font-bold transition-colors ${
                    paperSize === 'Letter' 
                      ? 'border-teal-600 bg-teal-50/70 text-teal-900' 
                      : 'border-slate-200 text-slate-600'
                  }`}
                >
                  کاغذ Letter
                </button>
              </div>
            </div>

            {/* Actions: Print, Share, Download */}
            <div className="grid grid-cols-2 gap-2 pt-2">
              <button
                onClick={() => {
                  triggerToast('دستور چاپ مستقیم به پرینتر ارسال شد');
                  setShowExportModal(false);
                }}
                className="flex items-center justify-center gap-1.5 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-800 rounded-xl text-xs font-bold border border-slate-300 transition-colors"
              >
                <Printer className="w-4 h-4" />
                <span>پرینت مستقیم</span>
              </button>

              <button
                onClick={() => {
                  triggerToast('منوی اشتراک‌گذاری سیستم‌عامل باز شد');
                  setShowExportModal(false);
                }}
                className="flex items-center justify-center gap-1.5 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-800 rounded-xl text-xs font-bold border border-slate-300 transition-colors"
              >
                <Share2 className="w-4 h-4" />
                <span>اشتراک‌گذاری</span>
              </button>
            </div>

            <button
              onClick={() => {
                triggerToast('فایل PDF با موفقیت تولید و در حافظه ذخیره شد');
                setShowExportModal(false);
              }}
              className="w-full flex items-center justify-center gap-2 py-3 bg-teal-700 hover:bg-teal-800 text-white rounded-xl text-xs font-bold shadow-md shadow-teal-700/30 transition-all"
            >
              <FileDown className="w-4 h-4" />
              <span>تولید و دانلود نهایی فایل PDF</span>
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
