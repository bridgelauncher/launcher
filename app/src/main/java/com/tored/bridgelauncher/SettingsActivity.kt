package com.tored.bridgelauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tored.bridgelauncher.ui.settings.SettingsScreen
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme

class SettingsActivity : ComponentActivity()
{
    private lateinit var _bridge: BridgeLauncherApplication

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        _bridge  = applicationContext as BridgeLauncherApplication

        setContent {
            BridgeLauncherTheme()
            {
                SettingsScreen(
                    onGrantPermissionRequest = { },
                    hasStoragePerms = false,
                )
            }
        }
    }
}
