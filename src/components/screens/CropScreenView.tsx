import React, { useState, useRef, useEffect } from 'react';
import { ArrowRight, RotateCw, Wand2, RefreshCw, Check } from 'lucide-react';
import { CropCorners } from '../../types';

interface CropScreenViewProps {
  onBack: () => void;
  onProceed: () => void;
}

export const CropScreenView: React.FC<CropScreenViewProps> = ({ onBack, onProceed }) => {
  const [rotation, setRotation] = useState(0);
  const [corners, setCorners] = useState<CropCorners>({
    topLeft: { x: 12, y: 15 },
    topRight: { x: 86, y: 12 },
    bottomRight: { x: 88, y: 88 },
    bottomLeft: { x: 10, y: 85 }
  });

  const [activeCorner, setActiveCorner] = useState<keyof CropCorners | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  // Handle dragging corners
  const handlePointerDown = (cornerKey: keyof CropCorners) => {
    setActiveCorner(cornerKey);
  };

  const handlePointerMove = (e: React.PointerEvent<HTMLDivElement>) => {
    if (!activeCorner || !containerRef.current) return;
    const rect = containerRef.current.getBoundingClientRect();
    const x = Math.max(2, Math.min(98, ((e.clientX - rect.left) / rect.width) * 100));
    const y = Math.max(2, Math.min(98, ((e.clientY - rect.top) / rect.height) * 100));

    setCorners(prev => ({
      ...prev,
      [activeCorner]: { x, y }
    }));
  };

  const handlePointerUp = () => {
    setActiveCorner(null);
  };

  const handleAutoDetect = () => {
    // Snap cleanly to simulated document margins
    setCorners({
      topLeft: { x: 15, y: 14 },
      topRight: { x: 85, y: 14 },
      bottomRight: { x: 85, y: 86 },
      bottomLeft: { x: 15, y: 86 }
    });
  };

  const handleReset = () => {
    setCorners({
      topLeft: { x: 8, y: 8 },
      topRight: { x: 92, y: 8 },
      bottomRight: { x: 92, y: 92 },
      bottomLeft: { x: 8, y: 92 }
    });
  };

  // SVG Polygon Points
  const polygonPoints = `${corners.topLeft.x}%,${corners.topLeft.y}% ${corners.topRight.x}%,${corners.topRight.y}% ${corners.bottomRight.x}%,${corners.bottomRight.y}% ${corners.bottomLeft.x}%,${corners.bottomLeft.y}%`;

  return (
    <div 
      className="flex flex-col h-full bg-slate-950 text-white relative select-none"
      onPointerMove={handlePointerMove}
      onPointerUp={handlePointerUp}
    >
      {/* Top Header */}
      <div className="flex items-center justify-between px-4 py-3 bg-slate-900 border-b border-slate-800 z-10">
        <div className="flex items-center gap-2">
          <button 
            onClick={onBack}
            className="p-1.5 rounded-full hover:bg-slate-800 text-slate-300"
          >
            <ArrowRight className="w-5 h-5" />
          </button>
          <span className="font-bold text-sm">تنظیم کادر برش سند</span>
        </div>
        <button
          onClick={handleReset}
          className="text-xs text-teal-400 hover:text-teal-300 font-semibold px-2 py-1"
        >
          ریست کادر
        </button>
      </div>

      {/* Guide tip */}
      <div className="bg-slate-900/90 text-slate-300 text-center text-[11px] py-1.5 px-3 border-b border-slate-800/80">
        دستگیره‌های ۴ گوشه را روی لبه‌های برگه تنظیم کنید
      </div>

      {/* Main Canvas Area */}
      <div 
        ref={containerRef}
        className="flex-1 relative overflow-hidden flex items-center justify-center p-4 bg-slate-950 cursor-crosshair"
      >
        {/* Document paper mockup */}
        <div 
          className="relative w-64 h-88 bg-[#f5f1e8] rounded-md shadow-2xl transition-transform duration-200 border border-slate-700/60 flex flex-col justify-between p-4"
          style={{ transform: `rotate(${rotation}deg)` }}
        >
          <div className="flex justify-between items-start border-b border-slate-300 pb-2">
            <div className="w-16 h-3 bg-slate-400/40 rounded-xs" />
            <div className="w-10 h-3 bg-slate-400/40 rounded-xs" />
          </div>
          <div className="space-y-2 py-2">
            <div className="w-3/4 h-2.5 bg-slate-400/50 rounded-xs" />
            <div className="w-full h-2 bg-slate-300/60 rounded-xs" />
            <div className="w-5/6 h-2 bg-slate-300/60 rounded-xs" />
            <div className="w-full h-2 bg-slate-300/60 rounded-xs" />
            <div className="w-4/5 h-2 bg-slate-300/60 rounded-xs" />
            <div className="w-2/3 h-2 bg-slate-300/60 rounded-xs" />
          </div>
          <div className="flex justify-end pt-2 border-t border-slate-300">
            <div className="w-12 h-12 rounded-lg border-2 border-teal-800/40 flex items-center justify-center text-[9px] font-bold text-teal-900/60">
              مهر رسمی
            </div>
          </div>
        </div>

        {/* SVG Overlay with crop box and handles */}
        <svg className="absolute inset-0 w-full h-full pointer-events-none">
          {/* Border polygon connecting the 4 corners */}
          <polygon
            points={polygonPoints}
            fill="rgba(13, 148, 136, 0.12)"
            stroke="#0d9488"
            strokeWidth="2.5"
            strokeDasharray="none"
          />

          {/* Grid lines inside */}
          <line
            x1={`${(corners.topLeft.x + corners.bottomLeft.x) / 2}%`}
            y1={`${(corners.topLeft.y + corners.bottomLeft.y) / 2}%`}
            x2={`${(corners.topRight.x + corners.bottomRight.x) / 2}%`}
            y2={`${(corners.topRight.y + corners.bottomRight.y) / 2}%`}
            stroke="#0d9488"
            strokeWidth="1"
            strokeDasharray="4 4"
            opacity="0.6"
          />
          <line
            x1={`${(corners.topLeft.x + corners.topRight.x) / 2}%`}
            y1={`${(corners.topLeft.y + corners.topRight.y) / 2}%`}
            x2={`${(corners.bottomLeft.x + corners.bottomRight.x) / 2}%`}
            y2={`${(corners.bottomLeft.y + corners.bottomRight.y) / 2}%`}
            stroke="#0d9488"
            strokeWidth="1"
            strokeDasharray="4 4"
            opacity="0.6"
          />
        </svg>

        {/* Interactive Grab Handles */}
        {(['topLeft', 'topRight', 'bottomRight', 'bottomLeft'] as const).map((key) => (
          <div
            key={key}
            onPointerDown={() => handlePointerDown(key)}
            className="absolute -translate-x-1/2 -translate-y-1/2 w-8 h-8 flex items-center justify-center cursor-grab active:cursor-grabbing z-20"
            style={{
              left: `${corners[key].x}%`,
              top: `${corners[key].y}%`
            }}
          >
            <div className="w-5 h-5 rounded-full bg-teal-500 border-2 border-white shadow-lg flex items-center justify-center" />
          </div>
        ))}
      </div>

      {/* Bottom Controls */}
      <div className="p-4 bg-slate-900 border-t border-slate-800 space-y-3 z-10">
        <div className="flex gap-2">
          <button
            onClick={handleAutoDetect}
            className="flex-1 flex items-center justify-center gap-1.5 py-2.5 bg-slate-800 hover:bg-slate-700 text-teal-300 rounded-xl text-xs font-bold border border-slate-700 transition-colors"
          >
            <Wand2 className="w-4 h-4" />
            <span>تشخیص خودکار لبه</span>
          </button>
          <button
            onClick={() => setRotation((r) => (r + 90) % 360)}
            className="flex-1 flex items-center justify-center gap-1.5 py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-xl text-xs font-bold border border-slate-700 transition-colors"
          >
            <RotateCw className="w-4 h-4" />
            <span>چرخش ۹۰°</span>
          </button>
        </div>

        <button
          onClick={onProceed}
          className="w-full flex items-center justify-center gap-2 py-3 bg-teal-600 hover:bg-teal-500 active:scale-98 text-white rounded-xl text-sm font-bold shadow-lg shadow-teal-600/30 transition-all"
        >
          <span>مرحله بعد: فیلترها و پیش‌نمایش</span>
          <Check className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
};
