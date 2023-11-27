package com.tored.bridgelauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tored.bridgelauncher.ui.screens.home.HomeScreen
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            BridgeLauncherTheme {
                HomeScreen()
            }
        }
    }
}
