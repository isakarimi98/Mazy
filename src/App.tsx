import React, { useState } from 'react';
import { 
  Smartphone, 
  FileCode, 
  Workflow, 
  Download, 
  ShieldCheck, 
  Layers, 
  CheckCircle2, 
  Terminal,
  ExternalLink,
  Github
} from 'lucide-react';
import { AndroidSimulator } from './components/AndroidSimulator';
import { CodeViewer } from './components/CodeViewer';

export default function App() {
  const [activeTab, setActiveTab] = useState<'simulator' | 'code' | 'cicd'>('simulator');

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-['Vazirmatn',sans-serif]">
      {/* Top Main Navigation Header */}
      <header className="border-b border-slate-800 bg-slate-900/90 backdrop-blur-md sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-teal-600 flex items-center justify-center text-white font-bold shadow-lg shadow-teal-600/30">
              <Layers className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="font-bold text-base sm:text-lg text-white">DocScan - اسکنر و فتوکپی اسناد</h1>
                <span className="text-[11px] bg-teal-950 text-teal-300 border border-teal-800/80 px-2 py-0.5 rounded-full font-semibold">
                  نیتیو کاتلین + Compose
                </span>
              </div>
              <p className="text-xs text-slate-400 hidden sm:block">
                فاز ۱: طراحی کامل UI/UX راست‌چین، ساختار معماری و خط لوله بیلد APK با GitHub Actions
              </p>
            </div>
          </div>

          {/* Tab Switcher */}
          <div className="flex items-center gap-1 bg-slate-800/80 p-1 rounded-xl border border-slate-700/60">
            <button
              onClick={() => setActiveTab('simulator')}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                activeTab === 'simulator'
                  ? 'bg-teal-600 text-white shadow-sm'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Smartphone className="w-4 h-4" />
              <span>شبیه‌ساز تعاملی</span>
            </button>

            <button
              onClick={() => setActiveTab('code')}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                activeTab === 'code'
                  ? 'bg-teal-600 text-white shadow-sm'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <FileCode className="w-4 h-4" />
              <span>فایل‌های کاتلین و Gradle</span>
            </button>

            <button
              onClick={() => setActiveTab('cicd')}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                activeTab === 'cicd'
                  ? 'bg-teal-600 text-white shadow-sm'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Workflow className="w-4 h-4" />
              <span>بیلد خودکار APK</span>
            </button>
          </div>
        </div>
      </header>

      {/* Main Content Area */}
      <main className="flex-1 max-w-7xl w-full mx-auto p-4 sm:p-6 flex flex-col">
        {activeTab === 'simulator' && (
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
            {/* Left/Center: Phone Mockup */}
            <div className="lg:col-span-6 flex justify-center">
              <AndroidSimulator />
            </div>

            {/* Right: Feature Highlights & Phase 1 Scope Verification */}
            <div className="lg:col-span-6 space-y-4 text-right">
              <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 shadow-xl space-y-4">
                <div className="flex items-center gap-2 text-teal-400">
                  <ShieldCheck className="w-6 h-6" />
                  <h2 className="font-bold text-lg text-white">ویژگی‌های پیاده‌سازی شده در فاز اول</h2>
                </div>
                <p className="text-sm text-slate-300 leading-relaxed">
                  تمامی صفحات و مؤلفه‌های تعاملی طبق نیازمندی‌های دقیق پروژه به زبان فارسی و استاندارد Material Design 3 پیاده‌سازی شده‌اند.
                </p>

                <div className="space-y-3 pt-2">
                  <div className="flex items-start gap-3 p-3 bg-slate-950/60 rounded-2xl border border-slate-800">
                    <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0 mt-0.5" />
                    <div>
                      <h4 className="text-sm font-bold text-slate-100">صفحه اصلی (Home/Dashboard)</h4>
                      <p className="text-xs text-slate-400 mt-0.5">
                        لیست اسناد اسکن‌شده، فیلتر علاقه‌مندی‌ها، جستجوی زنده، دکمه شناور بزرگ (FAB) با باتم‌شیت انتخاب دوربین یا گالری.
                      </p>
                    </div>
                  </div>

                  <div className="flex items-start gap-3 p-3 bg-slate-950/60 rounded-2xl border border-slate-800">
                    <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0 mt-0.5" />
                    <div>
                      <h4 className="text-sm font-bold text-slate-100">صفحه کادر برش با دستگیره‌های ۴ گوشه</h4>
                      <p className="text-xs text-slate-400 mt-0.5">
                        کادر برش چندضلعی با ۴ دستگیره تعاملی قابل درگ، تشخیص خودکار لبه‌های مدرک، چرخش ۹۰ درجه و شبکه خطوط راهنما.
                      </p>
                    </div>
                  </div>

                  <div className="flex items-start gap-3 p-3 bg-slate-950/60 rounded-2xl border border-slate-800">
                    <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0 mt-0.5" />
                    <div>
                      <h4 className="text-sm font-bold text-slate-100">۵ فیلتر تخصصی تمیزکاری اسناد</h4>
                      <p className="text-xs text-slate-400 mt-0.5">
                        شامل: ۱. اصلی، ۲. فتوکپی پرکنتراست، ۳. سیاه و سفید اداری، ۴. وایت‌بورد/حذف سایه، ۵. رنگی جادویی (تقویت مهر و امضا).
                      </p>
                    </div>
                  </div>

                  <div className="flex items-start gap-3 p-3 bg-slate-950/60 rounded-2xl border border-slate-800">
                    <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0 mt-0.5" />
                    <div>
                      <h4 className="text-sm font-bold text-slate-100">شیت خروجی، اشتراک‌گذاری و پرینت مستقیم</h4>
                      <p className="text-xs text-slate-400 mt-0.5">
                        انتخاب کیفیت خروجی (۳۰۰ DPI اداری یا فشرده)، اندازه کاغذ (A4 یا Letter)، اشتراک‌گذاری و ارسال دستور چاپ.
                      </p>
                    </div>
                  </div>
                </div>
              </div>

              {/* Offline & Marketplace Compliance Card */}
              <div className="bg-emerald-950/30 border border-emerald-800/40 rounded-3xl p-5 space-y-2 text-right">
                <div className="flex items-center gap-2 text-emerald-400 font-bold text-sm">
                  <ShieldCheck className="w-5 h-5" />
                  <span>آماده انتشار در کافه‌بازار و مایکت</span>
                </div>
                <p className="text-xs text-emerald-200/80 leading-relaxed">
                  اپلیکیشن به صورت ۱۰۰٪ آفلاین و محلی عمل می‌کند، هیچ دسترسی غیرضروری به اینترنت در AndroidManifest وجود ندارد و امنیت حریم خصوصی اسناد و مدارک کاربران تضمین شده است.
                </p>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'code' && (
          <div className="flex-1 h-[720px]">
            <CodeViewer />
          </div>
        )}

        {activeTab === 'cicd' && (
          <div className="max-w-4xl mx-auto w-full space-y-6 text-right">
            <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 sm:p-8 shadow-xl space-y-6">
              <div className="flex items-center gap-3 text-teal-400">
                <Workflow className="w-7 h-7" />
                <div>
                  <h2 className="font-bold text-xl text-white">پیکربندی خط لوله GitHub Actions (تولید خودکار APK)</h2>
                  <p className="text-xs text-slate-400 mt-0.5">مسیر فایل: <code className="text-teal-300 font-mono">.github/workflows/build-apk.yml</code></p>
                </div>
              </div>

              {/* Resolution for GitHub Actions Issue & Performance */}
              <div className="bg-emerald-950/30 border border-emerald-500/40 rounded-2xl p-4 text-right space-y-2">
                <div className="flex items-center gap-2 text-emerald-400 font-bold text-sm">
                  <CheckCircle2 className="w-5 h-5 text-emerald-400" />
                  <span>بررسی دقیق لاگ اجرای ران جدید و رفع ریشه‌ای مشکل:</span>
                </div>
                <div className="text-xs text-emerald-200/90 leading-relaxed space-y-1.5">
                  <p>
                    <strong>نتیجه بررسی زمان:</strong> خوشبختانه طبق خروجی لاگ گیت‌هاب اکشنز ران جدید (<code className="font-mono text-emerald-300">Run 34612601324</code>)، زمان اجرا از ۳۰ دقیقه به <strong>تنها ۱ دقیقه و ۵۶ ثانیه</strong> کاهش یافته است (مشکل هنگ کردن کاملاً برطرف شده).
                  </p>
                  <p>
                    <strong>ریشه توقف در ثانیه پایانی:</strong> در فایل منیفست اندروید (<code className="font-mono text-amber-300">AndroidManifest.xml</code>) به آیکون‌های اپلیکیشن (<code className="font-mono text-amber-300">@mipmap/ic_launcher</code> و <code className="font-mono text-amber-300">@mipmap/ic_launcher_round</code>) ارجاع داده شده بود اما این فایل‌های گرافیکی در پوشه منابع وجود نداشتند و سبب خطای کامپایل منابع (AAPT Resource Missing) شد.
                  </p>
                  <p>
                    <strong>اصلاحات ریشه‌ای اعمال‌شده:</strong>
                  </p>
                  <ul className="list-disc list-inside space-y-0.5 text-slate-300 pr-2">
                    <li>تولید کامل آیکون‌های استاندارد وکتور و Adaptive در مسیرهای <code className="font-mono text-teal-300">res/mipmap-anydpi-v26/</code> و <code className="font-mono text-teal-300">res/drawable/</code>.</li>
                    <li>بهینه‌سازی مخازن و تنظیم <code className="font-mono text-teal-300">repositoriesMode = PREFER_SETTINGS</code> در <code className="font-mono">settings.gradle.kts</code>.</li>
                    <li>اصلاح پیکربندی <code className="font-mono text-teal-300">app/build.gradle.kts</code> و هماهنگ‌سازی کامل نام پکیج و FileProvider.</li>
                    <li>افزودن لاگ‌های تفصیلی و ارسال گزارش عیب‌یابی در صورت بروز هرگونه خطای احتمالی در آینده.</li>
                  </ul>
                </div>
              </div>

              <div className="space-y-4 text-sm text-slate-300 leading-relaxed">
                <p>
                  این خط لوله با هر بار ارسال کد (<code className="text-teal-300 font-mono">push</code>) یا ایجاد پول ریکوئست به مخزن، روی سرور لینوکس گیت‌هاب اجرا می‌شود و فایل نصبی را آماده می‌کند:
                </p>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <div className="p-4 bg-slate-950 rounded-2xl border border-slate-800">
                    <div className="text-teal-400 font-bold text-sm flex items-center gap-2 mb-1">
                      <span className="w-6 h-6 rounded-full bg-teal-950 border border-teal-800 flex items-center justify-center text-xs">۱</span>
                      آماده‌سازی جاوا ۱۷
                    </div>
                    <p className="text-xs text-slate-400">نصب خودکار OpenJDK 17 توزیع Temurin به همراه فعال‌سازی کَش گریدل برای سرعت بیلد بالا.</p>
                  </div>

                  <div className="p-4 bg-slate-950 rounded-2xl border border-slate-800">
                    <div className="text-teal-400 font-bold text-sm flex items-center gap-2 mb-1">
                      <span className="w-6 h-6 rounded-full bg-teal-950 border border-teal-800 flex items-center justify-center text-xs">۲</span>
                      مجوز اجرایی به gradlew
                    </div>
                    <p className="text-xs text-slate-400">اجرای فرمان <code className="text-teal-300 font-mono">chmod +x ./gradlew</code> جهت جلوگیری از خطای Permission Denied.</p>
                  </div>

                  <div className="p-4 bg-slate-950 rounded-2xl border border-slate-800">
                    <div className="text-teal-400 font-bold text-sm flex items-center gap-2 mb-1">
                      <span className="w-6 h-6 rounded-full bg-teal-950 border border-teal-800 flex items-center justify-center text-xs">۳</span>
                      کامپایل و بیلد Debug
                    </div>
                    <p className="text-xs text-slate-400">اجرای دستور <code className="text-teal-300 font-mono">./gradlew assembleDebug --stacktrace</code> برای کامپایل کدهای کاتلین و Jetpack Compose.</p>
                  </div>

                  <div className="p-4 bg-slate-950 rounded-2xl border border-slate-800">
                    <div className="text-teal-400 font-bold text-sm flex items-center gap-2 mb-1">
                      <span className="w-6 h-6 rounded-full bg-teal-950 border border-teal-800 flex items-center justify-center text-xs">۴</span>
                      تولید Artifact قابل دانلود
                    </div>
                    <p className="text-xs text-slate-400">ذخیره خروجی <code className="text-teal-300 font-mono">app-debug.apk</code> در بخش Summary اجرای اکشن جهت دانلود مستقیم و نصب روی گوشی.</p>
                  </div>
                </div>

                {/* GitHub Actions Instructions */}
                <div className="bg-slate-950 p-4 rounded-2xl border border-slate-800 space-y-2">
                  <h4 className="font-bold text-slate-200 text-xs flex items-center gap-2">
                    <Terminal className="w-4 h-4 text-teal-400" />
                    دستورات محلی برای تست بیلد قبل از پوش به گیت‌هاب:
                  </h4>
                  <div className="p-3 bg-black/80 rounded-xl font-mono text-xs text-teal-300 space-y-1" dir="ltr">
                    <div>chmod +x ./gradlew</div>
                    <div>./gradlew assembleDebug --stacktrace</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
