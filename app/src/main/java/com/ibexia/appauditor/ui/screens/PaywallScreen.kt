//package com.ibexia.appauditor.ui.screens
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.compose.foundation.background
//
//@Composable
//fun PaywallScreen(onUpgradeClick: () -> Unit) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF032B3A))
//            .padding(horizontal = 24.dp, vertical = 40.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Take Back Your Privacy with Quollium Premium",
//            style = MaterialTheme.typography.headlineSmall,
//            color = Color.White
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text(
//            text = "Unlock the Full Device Audit (View all apps)",
//            style = MaterialTheme.typography.bodyMedium,
//            color = Color(0xFFD0E4EA)
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Text(
//            text = "One-Tap Permission Revocation",
//            style = MaterialTheme.typography.bodyMedium,
//            color = Color(0xFFD0E4EA)
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Text(
//            text = "Background Threat Monitoring (Alerts for new risky installs)",
//            style = MaterialTheme.typography.bodyMedium,
//            color = Color(0xFFD0E4EA)
//        )
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        Button(
//            onClick = { onUpgradeClick() },
//            modifier = Modifier.fillMaxWidth(),
//            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D5A))
//        ) {
//            Text(text = "Upgrade Now", style = MaterialTheme.typography.titleMedium)
//        }
//    }
//}


// all good with proper bullets for sprint 4
//package com.ibexia.appauditor.ui.screens
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun PaywallScreen(onUpgradeClick: () -> Unit) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF032B3A)) // Background color
//            .padding(horizontal = 24.dp, vertical = 32.dp), // Adjusted padding for top and bottom
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // Headline Section
//        Text(
//            text = "Take Back Your Privacy with Quollium Premium",
//            style = MaterialTheme.typography.headlineSmall,
//            color = Color.White,
//            modifier = Modifier.padding(bottom = 24.dp) // Reduced padding
//        )
//
//        // Bullet Point 1
//        BulletPoint(text = "Unlock the Full Device Audit (View all apps)")
//
//        // Bullet Point 2
//        BulletPoint(text = "One-Tap Permission Revocation")
//
//        // Bullet Point 3
//        BulletPoint(text = "Background Threat Monitoring (Alerts for new risky installs)")
//
//        Spacer(modifier = Modifier.height(32.dp)) // Space before the action button
//
//        // Upgrade Now Button
//        Button(
//            onClick = { onUpgradeClick() },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(55.dp), // Adjusted height for better click area
//            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D5A)),
//            shape = RoundedCornerShape(50)
//        ) {
//            Text(
//                text = "Upgrade Now",
//                style = MaterialTheme.typography.titleMedium,
//                color = Color.White
//            )
//        }
//    }
//}
//
//@Composable
//fun BulletPoint(text: String) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp) // Adjusted padding for alignment
//    ) {
//        // Bullet symbol (•)
//        Text(
//            text = "•",
//            style = MaterialTheme.typography.bodyMedium,
//            color = Color(0xFFFFB74D), // Orange color for bullet
//            modifier = Modifier.padding(end = 8.dp) // Spacing between bullet and text
//        )
//
//        // Text for the bullet point
//        Text(
//            text = text,
//            style = MaterialTheme.typography.bodyMedium,
//            color = Color(0xFFD0E4EA) // Light gray color for the text
//        )
//    }
//}

package com.ibexia.appauditor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
@Composable
fun PaywallScreen(
    onPremiumClick: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF032B3A)) // Background color
            .padding(horizontal = 24.dp, vertical = 32.dp), // Adjusted padding for top and bottom
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Headline Section
        Text(
            text = "Take Back Your Privacy with Quollium Premium",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier.padding(bottom = 24.dp) // Reduced padding
        )

        // Bullet Point 1
        BulletPoint(text = "Unlock the Full Device Audit (View all apps)")

        // Bullet Point 2
        BulletPoint(text = "One-Tap Permission Revocation")

        // Bullet Point 3
        BulletPoint(text = "Background Threat Monitoring (Alerts for new risky installs)")

        Spacer(modifier = Modifier.height(32.dp)) // Space before the action button

        // Upgrade Now Button
        Button(
            onClick = { /* Placeholder: No action for now */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp), // Adjusted height for better click area
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D5A)),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = "Upgrade Now",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }

        // Go to Premium Button (this is the one that navigates to PremiumDashboard)
        Spacer(modifier = Modifier.height(16.dp)) // Add some spacing between buttons
        Button(
            onClick = { onPremiumClick() }, // Navigate to Premium Dashboard
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp), // Adjusted height for better click area
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = "Go to Premium",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp) // Adjusted padding for alignment
    ) {
        // Bullet symbol (•)
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFFFB74D), // Orange color for bullet
            modifier = Modifier.padding(end = 8.dp) // Spacing between bullet and text
        )

        // Text for the bullet point
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFD0E4EA) // Light gray color for the text
        )
    }
}