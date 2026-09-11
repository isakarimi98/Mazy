import React, { useState } from 'react';
import { FileCode, Copy, Check, Terminal, FolderGit2, CheckCircle } from 'lucide-react';

interface FileDefinition {
  path: string;
  name: string;
  category: 'workflow' | 'gradle' | 'manifest' | 'kotlin' | 'res';
  language: string;
  code: string;
}

const ANDROID_FILES: FileDefinition[] = [
  {
    path: '.github/workflows/build-apk.yml',
    name: 'build-apk.yml',
    category: 'workflow',
    language: 'yaml',
    code: `name: Build Android Debug APK

on:
  push:
    branches: [ "main", "master" ]
  pull_request:
    branches: [ "main", "master" ]
  workflow_dispatch:

concurrency:
  group: \${{ github.workflow }}-\${{ github.ref }}
  cancel-in-progress: true

jobs:
  build:
    name: Build Debug APK
    runs-on: ubuntu-latest
    timeout-minutes: 10

    steps:
      - name: Checkout Code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: '8.4'
          cache-read-only: false

      - name: Prepare Gradlew Executable
        run: |
          sed -i 's/\\r$//' ./gradlew || true
          chmod +x ./gradlew
          mkdir -p gradle/wrapper
          if [ ! -s gradle/wrapper/gradle-wrapper.jar ]; then
            echo "Downloading gradle-wrapper.jar fallback..."
            curl -sS -L -o gradle/wrapper/gradle-wrapper.jar https://raw.githubusercontent.com/gradle/gradle/v8.4.0/gradle/wrapper/gradle-wrapper.jar
          fi

      - name: Build Debug APK
        run: ./gradlew assembleDebug --no-daemon --stacktrace

      - name: Upload APK Artifact
        uses: actions/upload-artifact@v4
        if: success()
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/*.apk
          retention-days: 7
          if-no-files-found: error`
  },
  {
    path: 'app/build.gradle.kts',
    name: 'app/build.gradle.kts',
    category: 'gradle',
    language: 'kotlin',
    code: `plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ir.docscan.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "ir.docscan.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("io.coil-kt:coil-compose:2.6.0")
}`
  },
  {
    path: 'app/src/main/AndroidManifest.xml',
    name: 'AndroidManifest.xml',
    category: 'manifest',
    language: 'xml',
    code: `<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-feature android:name="android.hardware.camera.any" android:required="false" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

    <application
        android:name=".DocScanApp"
        android:allowBackup="true"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.DocScan"
        tools:targetApi="34">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="\${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
    </application>
</manifest>`
  },
  {
    path: 'app/src/main/java/ir/docscan/app/ui/screens/home/HomeScreen.kt',
    name: 'HomeScreen.kt',
    category: 'kotlin',
    language: 'kotlin',
    code: `package ir.docscan.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ir.docscan.app.ui.screens.home.components.DocItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToCrop: (String) -> Unit,
    onNavigateToPreview: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("اسناد و مدارک (DocScan)") },
                actions = {
                    IconButton(onClick = { viewModel.toggleSearch(!state.isSearchActive) }) {
                        Icon(Icons.Default.Search, contentDescription = "جستجو")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.showNewScanOptions(true) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("اسکن سند جدید") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            items(state.documents, key = { it.id }) { doc ->
                DocItemCard(
                    doc = doc,
                    onClick = { onNavigateToPreview(doc.id) },
                    onFavoriteToggle = { viewModel.toggleFavorite(doc.id) },
                    onMenuClick = { }
                )
            }
        }
    }
}`
  },
  {
    path: 'app/src/main/java/ir/docscan/app/ui/screens/crop/CropAdjustScreen.kt',
    name: 'CropAdjustScreen.kt',
    category: 'kotlin',
    language: 'kotlin',
    code: `package ir.docscan.app.ui.screens.crop

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropAdjustScreen(
    docId: String,
    onNavigateBack: () -> Unit,
    onProceedToPreview: (String) -> Unit
) {
    var corners by remember {
        mutableStateOf(
            CropCorners(
                topLeft = Offset(100f, 160f),
                topRight = Offset(620f, 140f),
                bottomRight = Offset(640f, 920f),
                bottomLeft = Offset(80f, 900f)
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تنظیم کادر برش") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { onProceedToPreview(docId) },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("مرحله بعد: فیلترها و پیش‌نمایش")
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    ) { padding ->
        CropHandleOverlay(
            corners = corners,
            onCornerMoved = { index, offset -> /* Update handles */ },
            modifier = Modifier.padding(padding)
        )
    }
}`
  },
  {
    path: 'app/src/main/java/ir/docscan/app/ui/screens/preview/FilterPreviewScreen.kt',
    name: 'FilterPreviewScreen.kt',
    category: 'kotlin',
    language: 'kotlin',
    code: `package ir.docscan.app.ui.screens.preview

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import ir.docscan.app.data.model.DocFilterType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPreviewScreen(
    docId: String,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit
) {
    var activeFilter by remember { mutableStateOf(DocFilterType.PHOTOCOPY) }
    var showExportSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("پیش‌نمایش و اعمال فیلتر") },
                actions = {
                    Button(onClick = { showExportSheet = true }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Text("خروجی PDF")
                    }
                }
            )
        },
        bottomBar = {
            FilterSelectorBar(
                selectedFilter = activeFilter,
                onFilterSelected = { activeFilter = it }
            )
        }
    ) { padding ->
        // Render paper view with activeFilter (Photocopy, BW Office, Whiteboard, Magic Color, Original)
    }
}`
  }
];

export const CodeViewer: React.FC = () => {
  const [selectedFile, setSelectedFile] = useState<FileDefinition>(ANDROID_FILES[0]);
  const [copied, setCopied] = useState(false);

  const handleCopy = () => {
    navigator.clipboard.writeText(selectedFile.code);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="flex flex-col h-full bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-xl text-slate-200">
      {/* Header */}
      <div className="flex items-center justify-between px-4 py-3 bg-slate-950/80 border-b border-slate-800">
        <div className="flex items-center gap-2">
          <FolderGit2 className="w-5 h-5 text-teal-400" />
          <span className="font-bold text-sm text-slate-100">فایل‌های استاندارد پروژه اندروید استودیو</span>
        </div>
        <button
          onClick={handleCopy}
          className="flex items-center gap-1.5 px-3 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-lg text-xs font-semibold transition-colors border border-slate-700"
        >
          {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
          <span>{copied ? 'کپی شد!' : 'کپی کد'}</span>
        </button>
      </div>

      {/* File Selector Tabs */}
      <div className="flex overflow-x-auto gap-1 p-2 bg-slate-950/50 border-b border-slate-800 scrollbar-none">
        {ANDROID_FILES.map((file) => {
          const isSelected = selectedFile.path === file.path;
          return (
            <button
              key={file.path}
              onClick={() => setSelectedFile(file)}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-mono whitespace-nowrap transition-colors ${
                isSelected
                  ? 'bg-teal-950 text-teal-300 border border-teal-800/80 font-bold'
                  : 'text-slate-400 hover:bg-slate-800/60 hover:text-slate-300'
              }`}
            >
              <FileCode className="w-3.5 h-3.5" />
              <span>{file.name}</span>
            </button>
          );
        })}
      </div>

      {/* Path Display */}
      <div className="px-4 py-1.5 bg-slate-950 text-[11px] font-mono text-slate-400 border-b border-slate-800/60 flex items-center justify-between" dir="ltr">
        <span>Path: /{selectedFile.path}</span>
        <span className="uppercase text-[10px] bg-slate-800 px-1.5 py-0.5 rounded text-slate-300">
          {selectedFile.language}
        </span>
      </div>

      {/* Code Area */}
      <div className="flex-1 overflow-auto p-4 bg-slate-950 font-mono text-xs text-slate-300 leading-relaxed" dir="ltr">
        <pre>{selectedFile.code}</pre>
      </div>
    </div>
  );
};
