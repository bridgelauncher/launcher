package com.tored.bridgelauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tored.bridgelauncher.ui.screens.settings.SettingsScreen
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.utils.getIsExtStorageManager
import com.tored.bridgelauncher.utils.startExtStorageManagerPermissionActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : ComponentActivity()
{
    private var _isExtStorageManager by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        _isExtStorageManager = getIsExtStorageManager()

        setContent {
            BridgeLauncherTheme()
            {
                SettingsScreen(
                    _isExtStorageManager,
                    onGrantPermissionRequest = {
                        startExtStorageManagerPermissionActivity()
                    }
                )
            }
        }
    }

    override fun onResume()
    {
        super.onResume()
        _isExtStorageManager = getIsExtStorageManager()
    }
}
