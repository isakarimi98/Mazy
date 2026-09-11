package ir.docscan.app

import android.app.Application

/**
 * Main Application class for DocScan.
 * 100% Offline, Privacy-First Document Scanner & Photocopy Enhancer.
 */
class DocScanApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialization for offline local storage or crash logs if needed
    }
}
