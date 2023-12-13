package com.tored.bridgelauncher

import android.app.Application
import android.content.BroadcastReceiver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.tored.bridgelauncher.services.BridgeLauncherBroadcastReceiver
import com.tored.bridgelauncher.webview.jsapi.BridgeToJSAPI
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BridgeLauncherApp : Application()
{
    lateinit var installedAppsHolder: InstalledAppsStateHolder
    lateinit var consoleMessagesHolder: ConsoleMessagesHolder
    lateinit var broadcastReceiver: BroadcastReceiver
    lateinit var bridgeToJSAPI: BridgeToJSAPI

    var hasStoragePerms by mutableStateOf(false)

    override fun onCreate()
    {
        super.onCreate()

        installedAppsHolder = InstalledAppsStateHolder(this, this.packageManager).apply()
        {
            loadInstalledApps()
        }

        consoleMessagesHolder = ConsoleMessagesHolder()

        broadcastReceiver = BridgeLauncherBroadcastReceiver()

        ContextCompat.registerReceiver(
            this,
            broadcastReceiver,
            BridgeLauncherBroadcastReceiver.intentFilter,
            ContextCompat.RECEIVER_EXPORTED,
        )

        bridgeToJSAPI = BridgeToJSAPI()
    }
}