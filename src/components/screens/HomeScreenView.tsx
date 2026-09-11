import React, { useState } from 'react';
import { DocumentItem } from '../../types';
import { 
  FileText, 
  Search, 
  ShieldCheck, 
  Star, 
  Plus, 
  Camera, 
  Image as ImageIcon, 
  Sliders, 
  X,
  Clock,
  HardDrive
} from 'lucide-react';

interface HomeScreenViewProps {
  documents: DocumentItem[];
  onSelectDoc: (doc: DocumentItem) => void;
  onToggleFavorite: (id: string) => void;
  onStartNewScan: () => void;
}

export const HomeScreenView: React.FC<HomeScreenViewProps> = ({
  documents,
  onSelectDoc,
  onToggleFavorite,
  onStartNewScan
}) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [isSearchActive, setIsSearchActive] = useState(false);
  const [showBottomSheet, setShowBottomSheet] = useState(false);
  const [filterTab, setFilterTab] = useState<'all' | 'favorites'>('all');

  const filteredDocs = documents.filter(doc => {
    const matchesSearch = doc.title.toLowerCase().includes(searchQuery.toLowerCase()) || 
                          doc.dateShamsi.includes(searchQuery);
    const matchesTab = filterTab === 'all' || doc.isFavorite;
    return matchesSearch && matchesTab;
  });

  return (
    <div className="flex flex-col h-full bg-slate-50 text-slate-800 relative select-none">
      {/* Top App Bar */}
      <div className="bg-white px-4 pt-3 pb-3 border-b border-slate-200/80 shadow-xs z-10">
        {isSearchActive ? (
          <div className="flex items-center gap-2 bg-slate-100 rounded-xl px-3 py-1.5 border border-slate-300">
            <Search className="w-4 h-4 text-slate-400 shrink-0" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="جستجو در نام سند یا تاریخ..."
              autoFocus
              className="bg-transparent text-sm w-full outline-none text-slate-800 text-right placeholder-slate-400 font-medium"
            />
            <button 
              onClick={() => { setIsSearchActive(false); setSearchQuery(''); }}
              className="text-slate-400 hover:text-slate-600 p-0.5"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        ) : (
          <div className="flex items-center justify-between">
            <div className="text-right">
              <h1 className="text-lg font-bold text-slate-900 leading-tight">اسناد و فتوکپی‌ها</h1>
              <div className="flex items-center gap-1 mt-0.5 text-emerald-600">
                <ShieldCheck className="w-3.5 h-3.5" />
                <span className="text-[11px] font-semibold">۱۰۰٪ آفلاین و ذخیره محلی امن</span>
              </div>
            </div>
            <div className="flex items-center gap-1">
              <button 
                onClick={() => setIsSearchActive(true)}
                className="p-2 text-slate-600 hover:bg-slate-100 rounded-full transition-colors"
                title="جستجو"
              >
                <Search className="w-5 h-5" />
              </button>
            </div>
          </div>
        )}

        {/* Filter Pills */}
        <div className="flex gap-2 mt-3 pt-1 border-t border-slate-100">
          <button
            onClick={() => setFilterTab('all')}
            className={`px-3 py-1 text-xs font-semibold rounded-full transition-colors ${
              filterTab === 'all'
                ? 'bg-teal-700 text-white shadow-xs'
                : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
            }`}
          >
            همه اسناد ({documents.length})
          </button>
          <button
            onClick={() => setFilterTab('favorites')}
            className={`flex items-center gap-1 px-3 py-1 text-xs font-semibold rounded-full transition-colors ${
              filterTab === 'favorites'
                ? 'bg-teal-700 text-white shadow-xs'
                : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
            }`}
          >
            <Star className="w-3 h-3 fill-amber-400 text-amber-400" />
            نشان‌شده‌ها ({documents.filter(d => d.isFavorite).length})
          </button>
        </div>
      </div>

      {/* Document List View */}
      <div className="flex-1 overflow-y-auto p-4 space-y-3 pb-24">
        {filteredDocs.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-64 text-center text-slate-400 p-6">
            <div className="w-16 h-16 rounded-2xl bg-teal-50 flex items-center justify-center text-teal-600 mb-3">
              <FileText className="w-8 h-8" />
            </div>
            <p className="font-bold text-slate-700 text-sm">هیچ سندی یافت نشد</p>
            <p className="text-xs text-slate-400 mt-1">با زدن دکمه «اسکن سند جدید» اولین مدرک خود را ثبت کنید.</p>
          </div>
        ) : (
          filteredDocs.map((doc) => (
            <div
              key={doc.id}
              onClick={() => onSelectDoc(doc)}
              className="bg-white rounded-2xl p-3.5 border border-slate-200/80 shadow-xs hover:shadow-md hover:border-teal-300 transition-all cursor-pointer flex items-center justify-between group"
            >
              <div className="flex items-center gap-3">
                <div className="w-13 h-13 rounded-xl bg-teal-50 border border-teal-100 flex items-center justify-center text-teal-700 shrink-0">
                  <FileText className="w-7 h-7" />
                </div>
                <div>
                  <h3 className="font-bold text-sm text-slate-800 group-hover:text-teal-700 transition-colors">
                    {doc.title}
                  </h3>
                  <div className="flex items-center gap-2 mt-1">
                    <span className="text-[11px] bg-teal-50 text-teal-800 px-2 py-0.5 rounded-md font-semibold">
                      {doc.pageCount} صفحه
                    </span>
                    <span className="text-[11px] bg-slate-100 text-slate-600 px-2 py-0.5 rounded-md font-medium">
                      {doc.activeFilter === 'photocopy' && 'فتوکپی'}
                      {doc.activeFilter === 'bw_office' && 'سیاه‌وسفید'}
                      {doc.activeFilter === 'magic_color' && 'رنگی جادویی'}
                      {doc.activeFilter === 'whiteboard' && 'وایت‌بورد'}
                      {doc.activeFilter === 'original' && 'اصلی'}
                    </span>
                  </div>
                  <div className="flex items-center gap-2 mt-1 text-[11px] text-slate-400">
                    <span className="flex items-center gap-1">
                      <Clock className="w-3 h-3" />
                      {doc.dateShamsi}
                    </span>
                    <span>•</span>
                    <span className="flex items-center gap-1">
                      <HardDrive className="w-3 h-3" />
                      {doc.fileSize}
                    </span>
                  </div>
                </div>
              </div>

              <button
                onClick={(e) => {
                  e.stopPropagation();
                  onToggleFavorite(doc.id);
                }}
                className="p-2 text-slate-300 hover:text-amber-500 transition-colors"
                title="نشان کردن"
              >
                <Star className={`w-5 h-5 ${doc.isFavorite ? 'fill-amber-400 text-amber-400' : ''}`} />
              </button>
            </div>
          ))
        )}
      </div>

      {/* Floating Action Button (FAB) */}
      <div className="absolute bottom-5 left-5 z-20">
        <button
          onClick={() => setShowBottomSheet(true)}
          className="flex items-center gap-2 bg-teal-700 hover:bg-teal-800 active:scale-95 text-white font-bold px-4 py-3 rounded-2xl shadow-lg shadow-teal-700/30 transition-all"
        >
          <Plus className="w-5 h-5" />
          <span className="text-sm">اسکن سند جدید</span>
        </button>
      </div>

      {/* New Document Material Bottom Sheet */}
      {showBottomSheet && (
        <div 
          className="absolute inset-0 bg-slate-900/60 backdrop-blur-xs z-30 flex flex-col justify-end transition-opacity"
          onClick={() => setShowBottomSheet(false)}
        >
          <div 
            className="bg-white rounded-t-3xl p-5 shadow-2xl space-y-3 animate-in slide-in-from-bottom duration-200"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="w-12 h-1 bg-slate-300 rounded-full mx-auto mb-2" />
            <h3 className="font-bold text-base text-slate-900 text-right">افزودن سند جدید</h3>
            <p className="text-xs text-slate-500 text-right">نحوه ورود تصویر مدرک را انتخاب نمایید:</p>

            <div className="space-y-2 pt-2">
              <button
                onClick={() => {
                  setShowBottomSheet(false);
                  onStartNewScan();
                }}
                className="w-full flex items-center gap-3.5 p-3.5 bg-teal-50 hover:bg-teal-100/80 border border-teal-200/70 rounded-2xl text-right transition-colors"
              >
                <div className="w-11 h-11 rounded-xl bg-teal-700 flex items-center justify-center text-white shrink-0">
                  <Camera className="w-6 h-6" />
                </div>
                <div>
                  <h4 className="font-bold text-sm text-slate-900">عکاسی با دوربین</h4>
                  <p className="text-[11px] text-slate-500">تشخیص هوشمند لبه‌ها و تصحیح زاویه سند</p>
                </div>
              </button>

              <button
                onClick={() => {
                  setShowBottomSheet(false);
                  onStartNewScan();
                }}
                className="w-full flex items-center gap-3.5 p-3.5 bg-slate-50 hover:bg-slate-100 border border-slate-200 rounded-2xl text-right transition-colors"
              >
                <div className="w-11 h-11 rounded-xl bg-slate-700 flex items-center justify-center text-white shrink-0">
                  <ImageIcon className="w-6 h-6" />
                </div>
                <div>
                  <h4 className="font-bold text-sm text-slate-900">انتخاب از گالری</h4>
                  <p className="text-[11px] text-slate-500">بارگذاری تصاویر ذخیره‌شده در گالری گوشی</p>
                </div>
              </button>
            </div>

            <button
              onClick={() => setShowBottomSheet(false)}
              className="w-full py-2.5 text-center text-xs font-bold text-slate-500 hover:text-slate-800"
            >
              انصراف
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
