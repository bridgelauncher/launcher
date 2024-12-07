package com.tored.bridgelauncher.ui2.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.tored.bridgelauncher.ui2.settings.composables.SettingsScreen2
import com.tored.bridgelauncher.ui2.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.utils.bridgeLauncherApplication

private val TAG = SettingsScreenActivity::class.simpleName

class SettingsScreenActivity : ComponentActivity()
{
    private val _settingsScreenVM: SettingsScreenVM by viewModels { SettingsScreenVM.Factory }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            BridgeLauncherTheme {
                SettingsScreen2(
                    _settingsScreenVM,
                    requestFinish = { finish() }
                )
            }
        }
    }

    override fun onResume()
    {
        bridgeLauncherApplication.services.storagePermsHolder.notifyPermsMightHaveChanged()
        super.onResume()
    }

}