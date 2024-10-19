package com.tored.bridgelauncher.services

import android.content.BroadcastReceiver
import android.content.pm.PackageManager
import com.tored.bridgelauncher.ConsoleMessagesHolder
import com.tored.bridgelauncher.api.jsapi.BridgeToJSAPI
import com.tored.bridgelauncher.api.jsapi.JSToBridgeAPI
import com.tored.bridgelauncher.api.server.BridgeServer
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.iconpacks.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.settings.SettingsVM

data class BridgeServices(
    val packageManager: PackageManager,
    val settingsVM: SettingsVM,
    val storagePermsManager: PermsManager,

    val bridgeServer: BridgeServer,
    val installedAppsHolder: InstalledAppsHolder,
    val installedIconPacksHolder: InstalledIconPacksHolder,
    val consoleMessagesHolder: ConsoleMessagesHolder,
    val broadcastReceiver: BroadcastReceiver,
    val bridgeToJSAPI: BridgeToJSAPI,
    val jsToBridgeAPI: JSToBridgeAPI,
)
