package com.tored.bridgelauncher.services

import android.content.BroadcastReceiver
import android.content.pm.PackageManager
import com.tored.bridgelauncher.api.jsapi.BridgeToJSAPI
import com.tored.bridgelauncher.api.jsapi.JSToBridgeAPI
import com.tored.bridgelauncher.api.server.BridgeServer
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.devconsole.DevConsoleMessagesHolder
import com.tored.bridgelauncher.services.iconcache.IconCache
import com.tored.bridgelauncher.services.iconpackcache.IconPackCache
import com.tored.bridgelauncher.services.iconpacks.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.mockexport.MockExporter
import com.tored.bridgelauncher.services.perms.PermsManager
import com.tored.bridgelauncher.services.settings.SettingsHolder

data class BridgeServices(
    val packageManager: PackageManager,
    val settingsHolder: SettingsHolder,
    val storagePermsManager: PermsManager,

    val installedAppsHolder: InstalledAppsHolder,
    val installedIconPacksHolder: InstalledIconPacksHolder,
    val iconPackCache: IconPackCache,
    val iconCache: IconCache,

    val bridgeServer: BridgeServer,
    val consoleMessagesHolder: DevConsoleMessagesHolder,
    val broadcastReceiver: BroadcastReceiver,
    val bridgeToJSAPI: BridgeToJSAPI,
    val jsToBridgeAPI: JSToBridgeAPI,

    val mockExporter: MockExporter,
)
