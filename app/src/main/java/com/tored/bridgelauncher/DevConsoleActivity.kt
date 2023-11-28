package com.tored.bridgelauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tored.bridgelauncher.ui.screens.devconsole.DevConsoleScreenStateful
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DevConsoleActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            BridgeLauncherTheme {
                DevConsoleScreenStateful()
            }
        }
    }
}
