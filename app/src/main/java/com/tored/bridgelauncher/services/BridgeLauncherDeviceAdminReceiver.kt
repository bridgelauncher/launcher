package com.tored.bridgelauncher.services

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.edit
import com.tored.bridgelauncher.settings.SettingsState
import com.tored.bridgelauncher.settings.settingsDataStore
import com.tored.bridgelauncher.utils.writeBool
import kotlinx.coroutines.runBlocking

class BridgeLauncherDeviceAdminReceiver : DeviceAdminReceiver()
{
    override fun onEnabled(context: Context, intent: Intent)
    {
        writeIsDeviceAdminEnabled(context, true)
    }

    override fun onDisabled(context: Context, intent: Intent)
    {
        writeIsDeviceAdminEnabled(context, false)
    }

    private fun writeIsDeviceAdminEnabled(context: Context, isEnabled: Boolean)
    {
        runBlocking {
            context.settingsDataStore.edit { prefs ->
                prefs.writeBool(SettingsState::isDeviceAdminEnabled, isEnabled)
            }
        }
    }
}