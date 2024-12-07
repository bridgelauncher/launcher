package com.tored.bridgelauncher.services.perms

import android.content.Context
import android.util.Log
import com.tored.bridgelauncher.services.settings2.BridgeSettings
import com.tored.bridgelauncher.services.settings2.settingsDataStore
import com.tored.bridgelauncher.services.settings2.useBridgeSettingStateFlow
import com.tored.bridgelauncher.utils.CurrentAndroidVersion
import com.tored.bridgelauncher.utils.checkCanSetSystemNightMode
import com.tored.bridgelauncher.utils.checkStoragePerms
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

private val TAG = PermsHolder::class.simpleName

class PermsHolder(
    private val _context: Context,
)
{
    private val _scope = CoroutineScope(Dispatchers.Main)

    private val _hasStoragePermsState = MutableStateFlow(_context.checkStoragePerms())
    val hasStoragePermsState = _hasStoragePermsState.asStateFlow()

    private val _canSetSystemNightModeState = MutableStateFlow(_context.checkCanSetSystemNightMode())
    val canSetSystemNightModeState = _canSetSystemNightModeState.asStateFlow()

    private val _isAccessibilityServiceEnabled = useBridgeSettingStateFlow(_context.settingsDataStore, _scope, BridgeSettings.isAccessibilityServiceEnabled)
    private val _isDeviceAdminEnabled = useBridgeSettingStateFlow(_context.settingsDataStore, _scope, BridgeSettings.isDeviceAdminEnabled)
    private val _allowProjectsToTurnScreenOff = useBridgeSettingStateFlow(_context.settingsDataStore, _scope, BridgeSettings.allowProjectsToTurnScreenOff)

    private fun _getCanProjectsLockScreen(acc: Boolean, adm: Boolean, allow: Boolean): Boolean
    {
        return if (CurrentAndroidVersion.supportsAccessiblityServiceScreenLock())
            acc && allow
        else
            adm && allow
    }

    val canProjectsLockScreen = combine(
        _isAccessibilityServiceEnabled,
        _isDeviceAdminEnabled,
        _allowProjectsToTurnScreenOff
    )
    { acc, adm, allow -> _getCanProjectsLockScreen(acc, adm, allow) }
        .stateIn(
            _scope,
            SharingStarted.Eagerly,
            _getCanProjectsLockScreen(
                _isAccessibilityServiceEnabled.value,
                _isDeviceAdminEnabled.value,
                _allowProjectsToTurnScreenOff.value
            )
        )


    // intended to be called from onResume() - there is no API to listen for permission changes, so checks in onResume it is
    fun notifyPermsMightHaveChanged()
    {
        val hasStoragePerms = _context.checkStoragePerms()
        val canSetSystemNightMode = _context.checkCanSetSystemNightMode()
        Log.d(TAG, "notifyPermsMightHaveChanged: hasStoragePerms = $hasStoragePerms, canSetSystemNightMode = $canSetSystemNightMode")
        _hasStoragePermsState.value = hasStoragePerms
        _canSetSystemNightModeState.value = canSetSystemNightMode
    }
}