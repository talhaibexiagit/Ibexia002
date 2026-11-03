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
                },
                onFixNowClick = { packageName ->
                    navController.navigate("revoke_screen/$packageName")
                }
            )
        }
        composable("paywall/{packageName}") { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName")
            PaywallScreen(
                onUpgradeClick = {
                    viewModel.setPremium(true)
                },
                onUnlockAppClick = {
                    viewModel.unlockOneMoreApp()
                    navController.popBackStack()
                },
                onPremiumClick = {
                    viewModel.setPremium(true)
                    navController.navigate("premium_dashboard")
                }
            )
        }
        composable("premium_dashboard") {
            PremiumDashboardScreen(
                viewModel = viewModel,
                onFixNowClick = { packageName ->
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
