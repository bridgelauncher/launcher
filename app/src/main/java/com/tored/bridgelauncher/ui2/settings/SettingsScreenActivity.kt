package com.tored.bridgelauncher.ui2.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.ui2.settings.composables.SettingsScreen2

private val TAG = SettingsScreenActivity::class.simpleName

class SettingsScreenActivity : ComponentActivity()
{
    private val _settingsScreenVM: SettingsScreenVM by viewModels { SettingsScreenVM.Factory }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            BridgeLauncherTheme {
                SettingsScreen2(_settingsScreenVM)
            }
        }
    }

}