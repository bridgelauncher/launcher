package com.tored.bridgelauncher.services

import android.content.Context
import com.tored.bridgelauncher.utils.checkCanSetSystemNightMode
import com.tored.bridgelauncher.utils.checkStoragePerms
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermsManager(
    private val _context: Context,
)
{
    private val _hasStoragePermsState = MutableStateFlow(_context.checkStoragePerms())
    val hasStoragePermsState = _hasStoragePermsState.asStateFlow()

    private val _canCheckSystemNightModeState = MutableStateFlow(_context.checkCanSetSystemNightMode())
    val canCheckSystemNightModeState = _canCheckSystemNightModeState.asStateFlow()

    private val _canLockScreenState = MutableStateFlow(false)

    // intended to be called from onResume() - there is no API to listen for permission changes, so checks in onResume it is
    fun notifyPermsMightHaveChanged()
    {
        _hasStoragePermsState.value = _context.checkStoragePerms()
    }
}