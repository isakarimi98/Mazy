package ir.docscan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import ir.docscan.app.ui.navigation.AppNavHost
import ir.docscan.app.ui.theme.DocScanTheme

/**
 * Main Activity for DocScan Android Application.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DocScanTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}
