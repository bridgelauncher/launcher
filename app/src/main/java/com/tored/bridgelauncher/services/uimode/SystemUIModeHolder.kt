package com.tored.bridgelauncher.services.uimode

import android.app.UiModeManager
import android.util.Log
import com.tored.bridgelauncher.api2.shared.SystemNightModeStringOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private val TAG = SystemUIModeHolder::class.simpleName

class SystemUIModeHolder(
    private val _uiModeManager: UiModeManager,
)
{
    private val _systemNightMode = MutableStateFlow(SystemNightModeStringOptions.Auto)
    val systemNightMode = _systemNightMode.asStateFlow()

    fun onConfigurationChanged()
    {
        SystemNightModeStringOptions.fromUiModeManagerNightMode(_uiModeManager.nightMode).let {
            if (it != systemNightMode.value)
            {
                Log.d(TAG, "onConfigurationChanged: $it")
                _systemNightMode.value = it
            }
        }
    }
}