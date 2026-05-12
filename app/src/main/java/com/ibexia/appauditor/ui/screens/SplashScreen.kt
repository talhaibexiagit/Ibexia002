//package com.ibexia.appauditor.ui.screens
//
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Card
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.delay
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.height
//import androidx.compose.material3.CardDefaults
//import androidx.compose.ui.graphics.Color
//
//@Composable
//fun AppAuditorRoot(
//    viewModel: com.ibexia.appauditor.viewmodel.AppViewModel
//) {
//    var showSplash by remember { mutableStateOf(true) }
//
//    LaunchedEffect(Unit) {
//        delay(2000)
//        showSplash = false
//    }
//
//    if (showSplash) {
//        SplashScreen()
//    } else {
//        DashboardScreen(viewModel = viewModel)
//    }
//}
//@Composable
//fun SplashScreen() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF032B3A))
//            .padding(horizontal = 24.dp, vertical = 40.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "🛡️",
//            style = MaterialTheme.typography.displayLarge,
//            color = Color.White
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text(
//            text = "Quollium Privacy Auditor",
//            style = MaterialTheme.typography.headlineSmall,
//            color = Color.White
//        )
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        Card(
//            colors = CardDefaults.cardColors(
//                containerColor = Color(0xFF0A4155)
//            ),
//            shape = RoundedCornerShape(16.dp),
//            modifier = Modifier.padding(top = 8.dp)
//        ) {
//            Text(
//                text = "100% Offline. Your data never leaves your device.",
//                modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
//                style = MaterialTheme.typography.bodyMedium,
//                color = Color(0xFFD9EEF5)
//            )
//        }
//    }
//}



package com.ibexia.appauditor.ui.screens

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SplashScreen(navController: NavController) {
    var showSplash by remember { mutableStateOf(true) }

    // Delay for splash screen before navigating to DashboardScreen
    LaunchedEffect(Unit) {
        // This delay simulates a splash screen that lasts for 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            showSplash = false
            // Navigate to Dashboard screen after splash
            navController.navigate("dashboard")
        }, 2000) // 2 seconds delay
    }

    if (showSplash) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF032B3A))
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🛡️",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Quollium Privacy Auditor",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0A4155)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "100% Offline. Your data never leaves your device.",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD9EEF5)
                )
            }
        }
    }
}