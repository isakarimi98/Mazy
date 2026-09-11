import React, { useState } from 'react';
import { Wifi, Battery, ShieldAlert, WifiOff } from 'lucide-react';
import { DocumentItem, AppScreen } from '../types';
import { INITIAL_DOCUMENTS } from '../data/mockDocuments';
import { HomeScreenView } from './screens/HomeScreenView';
import { CropScreenView } from './screens/CropScreenView';
import { PreviewScreenView } from './screens/PreviewScreenView';

export const AndroidSimulator: React.FC = () => {
  const [currentScreen, setCurrentScreen] = useState<AppScreen>('home');
  const [documents, setDocuments] = useState<DocumentItem[]>(INITIAL_DOCUMENTS);
  const [selectedDoc, setSelectedDoc] = useState<DocumentItem>(INITIAL_DOCUMENTS[0]);

  const handleToggleFavorite = (id: string) => {
    setDocuments(prev => prev.map(d => d.id === id ? { ...d, isFavorite: !d.isFavorite } : d));
  };

  const handleSelectDoc = (doc: DocumentItem) => {
    setSelectedDoc(doc);
    setCurrentScreen('preview');
  };

  const handleStartNewScan = () => {
    const newDoc: DocumentItem = {
      id: `doc-${Date.now()}`,
      title: 'سند اسکن‌شده جدید',
      dateShamsi: '۱۴۰۳/۰۶/۲۱',
      pageCount: 1,
      fileSize: '۱.۱ مگابایت',
      activeFilter: 'photocopy',
      isFavorite: false,
      sampleImage: 'contract'
    };
    setSelectedDoc(newDoc);
    setCurrentScreen('crop');
  };

  return (
    <div className="flex items-center justify-center p-2 sm:p-6 w-full h-full">
      {/* Phone Mockup Frame */}
      <div className="w-full max-w-[390px] h-[780px] bg-slate-900 rounded-[48px] p-3 shadow-2xl border-4 border-slate-700/80 ring-1 ring-white/10 flex flex-col relative overflow-hidden">
        {/* Notch / Speaker bar */}
        <div className="absolute top-4 left-1/2 -translate-x-1/2 w-28 h-4 bg-slate-950 rounded-full flex items-center justify-center z-40">
          <div className="w-10 h-1.5 bg-slate-800 rounded-full" />
          <div className="w-2.5 h-2.5 rounded-full bg-slate-900 border border-slate-800 ml-2" />
        </div>

        {/* Screen Bezel */}
        <div className="w-full h-full bg-slate-900 rounded-[38px] overflow-hidden flex flex-col relative">
          {/* Android Status Bar */}
          <div className="h-7 bg-white text-slate-800 flex items-center justify-between px-6 pt-1 text-xs font-semibold select-none z-30 shrink-0">
            <span className="font-mono text-[11px] text-slate-700">۱۲:۳۰</span>
            <div className="flex items-center gap-2">
              <span className="text-[10px] text-emerald-700 font-bold flex items-center gap-0.5">
                <WifiOff className="w-3 h-3 text-emerald-600" />
                آفلاین
              </span>
              <Battery className="w-3.5 h-3.5 text-slate-700" />
            </div>
          </div>

          {/* Screen Content Container */}
          <div className="flex-1 relative overflow-hidden bg-slate-50">
            {currentScreen === 'home' && (
              <HomeScreenView
                documents={documents}
                onSelectDoc={handleSelectDoc}
                onToggleFavorite={handleToggleFavorite}
                onStartNewScan={handleStartNewScan}
              />
            )}

            {currentScreen === 'crop' && (
              <CropScreenView
                onBack={() => setCurrentScreen('home')}
                onProceed={() => setCurrentScreen('preview')}
              />
            )}

            {currentScreen === 'preview' && (
              <PreviewScreenView
                document={selectedDoc}
                onBack={() => setCurrentScreen('home')}
                onDone={() => {
                  setDocuments(prev => [selectedDoc, ...prev.filter(d => d.id !== selectedDoc.id)]);
                  setCurrentScreen('home');
                }}
              />
            )}
          </div>

          {/* Android System Navigation Indicator */}
          <div className="h-4 bg-slate-900 flex items-center justify-center shrink-0">
            <div className="w-28 h-1 bg-slate-600 rounded-full" />
          </div>
        </div>
      </div>
    </div>
  );
};
