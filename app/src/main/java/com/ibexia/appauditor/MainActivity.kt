//package com.ibexia.appauditor//package com.ibexia.appauditor
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.viewModels
//import com.ibexia.appauditor.ui.screens.AppAuditorRoot
//import com.ibexia.appauditor.ui.theme.AppAuditorTheme
//import com.ibexia.appauditor.viewmodel.AppViewModel
//
//class MainActivity : ComponentActivity() {
//
//    private val viewModel: AppViewModel by viewModels()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            AppAuditorTheme {
//                AppAuditorRoot(viewModel = viewModel)
//            }
//        }
//    }
//}
//
//
//


package com.ibexia.appauditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.ibexia.appauditor.ui.NavGraph
import com.ibexia.appauditor.ui.theme.AppAuditorTheme
import com.ibexia.appauditor.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppAuditorTheme {
                NavGraph(viewModel = viewModel) // Use the NavGraph for navigation
            }
        }
    }
}