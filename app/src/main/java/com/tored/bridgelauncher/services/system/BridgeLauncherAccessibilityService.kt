package com.tored.bridgelauncher.services.system

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import androidx.datastore.preferences.core.edit
import com.tored.bridgelauncher.services.settings.SettingsState
import com.tored.bridgelauncher.services.settings.settingsDataStore
import com.tored.bridgelauncher.utils.writeBool
import kotlinx.coroutines.runBlocking


class BridgeLauncherAccessibilityService : AccessibilityService()
{
    companion object {
        var instance: BridgeLauncherAccessibilityService? = null
            private set
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?)
    {
    }

    override fun onInterrupt()
    {
    }

    override fun onServiceConnected()
    {
        instance = this
        writeIsAccessibilityServiceEnabled(true)
    }

    override fun onUnbind(intent: Intent?): Boolean
    {
        writeIsAccessibilityServiceEnabled(false)
        instance = null
        return super.onUnbind(intent)
    }

    private fun writeIsAccessibilityServiceEnabled(isEnabled: Boolean)
    {
        runBlocking {
            settingsDataStore.edit { prefs ->
                prefs.writeBool(SettingsState::isAccessibilityServiceEnabled, isEnabled)
            }
        }
    }
}