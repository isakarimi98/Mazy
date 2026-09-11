# اپلیکیشن اسکنر اسناد و مدارک (DocScan)
### پروژه نیتیو اندروید با کاتلین (Kotlin) و فریم‌ورک Jetpack Compose

این پروژه یک اپلیکیشن کاملاً آفلاین، سریع و امن برای اسکن اسناد و مدارک و تبدیل تصاویر به نسخه فتوکپی و اسکن‌شده تمیز (با قابلیت چاپ و خروجی PDF) بر اساس زبان طراحی **Material Design 3** و چینش استاندارد راست‌چین (**RTL**) است.

---

## 📱 ساختار پروژه اندروید
```
├── .github/
│   └── workflows/
│       └── build-apk.yml               # خودکارسازی بیلد APK با GitHub Actions
├── app/
│   ├── build.gradle.kts                # تنظیمات بیلد ماژول، کتابخانه‌های Compose و پلاگین‌ها
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml     # دسترسی‌های دوربین، حافظه و تعریف FileProvider
│           ├── java/ir/docscan/app/
│           │   ├── DocScanApp.kt       # کلاس Application
│           │   ├── MainActivity.kt     # اکتیویتی اصلی و راه‌اندازی تم و روتینگ
│           │   ├── data/
│           │   │   ├── model/          # کلاس‌های داده‌ای اسناد و فیلترها
│           │   │   └── mock/           # داده‌های تستی اولیه به زبان فارسی
│           │   └── ui/
│           │       ├── theme/          # تم، رنگ‌بندی Material 3 و تایپوگرافی فارسی
│           │       ├── navigation/     # روت‌های ناوبری (Home, Crop, Preview)
│           │       └── screens/
│           │           ├── home/       # صفحه اصلی (داشبورد، لیست اسناد، FAB دوربین/گالری)
│           │           ├── crop/       # صفحه تنظیم کادر برش با دستگیره‌های ۴ گوشه
│           │           ├── preview/    # صفحه پیش‌نمایش و ۵ حالت فیلتر تصویر
│           │           └── export/     # شیت خروجی PDF، اشتراک‌گذاری و پرینت مستقیم
│           └── res/
│               ├── values/             # رشته‌های متنی فارسی، رنگ‌ها و استایل‌ها
│               └── xml/                # تنظیمات امنیتی FileProvider و پشتیبان‌گیری
├── build.gradle.kts                    # اسکریپت ریشه بیلد گریدل
├── settings.gradle.kts                 # پیکربندی مخازن Maven و ماژول‌ها
├── gradle.properties                   # تنظیمات بهینه‌سازی JVM گریدل
└── gradlew                             # اسکریپت اجرای گریدل در سیستم‌عامل‌های یونیکس/مک
```

---

## 🚀 خط لوله بیلد خودکار GitHub Actions
فایل ورک‌فلو در مسیر `.github/workflows/build-apk.yml` قرار دارد:
- با هر `push` یا `pull_request` به شاخه‌های `main` یا `master` اجرا می‌شود.
- محیط JDK 17 تمورین را تنظیم می‌کند.
- دستور `./gradlew assembleDebug --stacktrace` را اجرا می‌نماید.
- فایل نصبی `app-debug.apk` را در تب **Actions** گیت‌هاب به عنوان **Artifact** آماده دانلود و تست قرار می‌دهد.

---

## 🛠️ اجرای دستی در Android Studio
1. پوشه پروژه را در **Android Studio Iguana** یا نسخه‌های جدیدتر باز کنید (`Open Project`).
2. اجازه دهید فرآیند **Gradle Sync** به پایان برسد.
3. با اتصال گوشی یا شبیه‌ساز (Emulator)، دکمه **Run (Shift + F10)** را بزنید.
