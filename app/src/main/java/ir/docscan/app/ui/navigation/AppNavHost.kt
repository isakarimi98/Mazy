package ir.docscan.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ir.docscan.app.ui.screens.crop.CropAdjustScreen
import ir.docscan.app.ui.screens.home.HomeScreen
import ir.docscan.app.ui.screens.home.HomeViewModel
import ir.docscan.app.ui.screens.preview.FilterPreviewScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val homeViewModel: HomeViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToCrop = { docId ->
                    navController.navigate(Screen.CropAdjust.createRoute(docId))
                },
                onNavigateToPreview = { docId ->
                    navController.navigate(Screen.FilterPreview.createRoute(docId))
                }
            )
        }

        // Crop & Quad Handle Adjust Screen
        composable(
            route = Screen.CropAdjust.route,
            arguments = listOf(navArgument("docId") { type = NavType.StringType })
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getString("docId") ?: "doc-new"
            CropAdjustScreen(
                docId = docId,
                onNavigateBack = { navController.popBackStack() },
                onProceedToPreview = { id ->
                    navController.navigate(Screen.FilterPreview.createRoute(id)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        // Filter Preview & Export Screen
        composable(
            route = Screen.FilterPreview.route,
            arguments = listOf(navArgument("docId") { type = NavType.StringType })
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getString("docId") ?: "doc-1"
            FilterPreviewScreen(
                docId = docId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
