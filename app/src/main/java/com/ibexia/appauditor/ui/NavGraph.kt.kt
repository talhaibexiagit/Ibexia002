//package com.ibexia.appauditor.ui
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.ibexia.appauditor.ui.screens.DashboardScreen
//import com.ibexia.appauditor.ui.screens.PaywallScreen
//import com.ibexia.appauditor.ui.screens.PremiumDashboardScreen
//import com.ibexia.appauditor.ui.screens.SplashScreen
//import com.ibexia.appauditor.viewmodel.AppViewModel
//import com.ibexia.appauditor.ui.screens.RevokeScreen
//
//
//@Composable
//fun NavGraph(viewModel: AppViewModel) {
//    val navController = rememberNavController()
//
//    NavHost(navController = navController, startDestination = "splash") {
//        composable("splash") {
//            SplashScreen(navController = navController)
//        }
//        composable("dashboard") {
//            DashboardScreen(
//                viewModel = viewModel,
//                onLockedAppClick = { app ->
//                    // Navigate to paywall for locked apps
//                    navController.navigate("paywall/${app.packageName}")
//                }
//            )
//        }
//        composable("paywall/{packageName}") { backStackEntry ->
//            val packageName = backStackEntry.arguments?.getString("packageName")
//            PaywallScreen(
//                onUpgradeClick = {
//                    // Handle the "Upgrade Now" action (Google Play Billing integration)
//                },
//                onPremiumClick = {
//                    // Navigate to Premium Dashboard Screen
//                    navController.navigate("premium_dashboard")
//                }
//            )
//        }
//        composable("premium_dashboard") {
//            // This is the Premium Dashboard Screen (Screen 4)
//            PremiumDashboardScreen(viewModel = viewModel)
//        }
//
//
//    }
//}


package com.ibexia.appauditor.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ibexia.appauditor.ui.screens.DashboardScreen
import com.ibexia.appauditor.ui.screens.PaywallScreen
import com.ibexia.appauditor.ui.screens.PremiumDashboardScreen
import com.ibexia.appauditor.ui.screens.RevokeScreen
import com.ibexia.appauditor.ui.screens.SplashScreen
import com.ibexia.appauditor.viewmodel.AppViewModel

@Composable
fun NavGraph(viewModel: AppViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onLockedAppClick = { app ->
                    navController.navigate("paywall/${app.packageName}")
                }
            )
        }
        composable("paywall/{packageName}") { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName")
            PaywallScreen(
                onUpgradeClick = {
                    // Handle the "Upgrade Now" action (Google Play Billing integration)
                },
                onPremiumClick = {
                    // Navigate to Premium Dashboard Screen
                    navController.navigate("premium_dashboard")
                }
            )
        }
        composable("premium_dashboard") {
            PremiumDashboardScreen(
                viewModel = viewModel,
                onFixNowClick = { packageName ->
                    // Navigate to the RevokeScreen
                    navController.navigate("revoke_screen/$packageName")
                }
            )
        }
        composable("revoke_screen/{packageName}") { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName")
            if (packageName != null) {
                RevokeScreen(packageName = packageName, viewModel = viewModel)
            }

        }
    }
}