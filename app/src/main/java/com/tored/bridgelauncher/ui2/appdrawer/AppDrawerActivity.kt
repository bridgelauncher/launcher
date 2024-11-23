package com.tored.bridgelauncher.ui2.appdrawer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.ui2.appdrawer.composables.AppDrawerScreen

class AppDrawerActivity : ComponentActivity()
{
    private val _appDrawerVM: AppDrawerVM by viewModels { AppDrawerVM.Factory }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            BridgeLauncherTheme {
                AppDrawerScreen(
                    vm = _appDrawerVM,
                    requestFinish = { finish() }
                )
            }
        }
    }
}