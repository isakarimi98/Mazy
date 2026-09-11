package ir.docscan.app.ui.navigation

/**
 * Sealed class for app navigation routes.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CropAdjust : Screen("crop_adjust/{docId}") {
        fun createRoute(docId: String) = "crop_adjust/$docId"
    }
    object FilterPreview : Screen("filter_preview/{docId}") {
        fun createRoute(docId: String) = "filter_preview/$docId"
    }
}
