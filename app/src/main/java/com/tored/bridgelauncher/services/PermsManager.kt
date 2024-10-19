package com.tored.bridgelauncher.services

import android.content.Context
import android.util.Log
import com.tored.bridgelauncher.utils.checkCanSetSystemNightMode
import com.tored.bridgelauncher.utils.checkStoragePerms
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private val TAG = PermsManager::class.simpleName

class PermsManager(
    private val _context: Context,
)
{
    private val _hasStoragePermsState = MutableStateFlow(_context.checkStoragePerms())
    val hasStoragePermsState = _hasStoragePermsState.asStateFlow()

    private val _canCheckSystemNightModeState = MutableStateFlow(_context.checkCanSetSystemNightMode())
    val canCheckSystemNightModeState = _canCheckSystemNightModeState.asStateFlow()

    // intended to be called from onResume() - there is no API to listen for permission changes, so checks in onResume it is
    fun notifyPermsMightHaveChanged()
    {
        val hasStoragePerms = _context.checkStoragePerms()
        val canSetSystemNightMode = _context.checkCanSetSystemNightMode()
        Log.d(TAG, "notifyPermsMightHaveChanged: hasStoragePerms = $hasStoragePerms, canSetSystemNightMode = $canSetSystemNightMode")
        _hasStoragePermsState.value = hasStoragePerms
        _canCheckSystemNightModeState.value = canSetSystemNightMode
    }
}