package com.ibexia.appauditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.ibexia.appauditor.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[AppViewModel::class.java]

       // viewModel.getUserInstalledApps()

        setContent {

        }
    }
}