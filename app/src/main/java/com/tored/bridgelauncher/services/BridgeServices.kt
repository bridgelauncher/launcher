package com.tored.bridgelauncher.services

import android.app.UiModeManager
import android.content.pm.PackageManager
import com.tored.bridgelauncher.api2.bridgetojs.BridgeToJSAPI
import com.tored.bridgelauncher.api2.jstobridge.JSToBridgeAPI
import com.tored.bridgelauncher.api2.server.BridgeServer
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.devconsole.DevConsoleMessagesHolder
import com.tored.bridgelauncher.services.displayshape.DisplayShapeHolder
import com.tored.bridgelauncher.services.iconcache.IconCache
import com.tored.bridgelauncher.services.iconpackcache.IconPackCache
import com.tored.bridgelauncher.services.iconpackcache.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.lifecycleevents.LifecycleEventsHolder
import com.tored.bridgelauncher.services.mockexport.MockExporter
import com.tored.bridgelauncher.services.perms.PermsHolder
import com.tored.bridgelauncher.services.system.BridgeLauncherBroadcastReceiver
import com.tored.bridgelauncher.services.uimode.SystemUIModeHolder
import com.tored.bridgelauncher.services.windowinsetsholder.WindowInsetsHolder

data class BridgeServices(
    // system
    val packageManager: PackageManager,
    val uiModeManager: UiModeManager,
    val broadcastReceiver: BridgeLauncherBroadcastReceiver,

    // state holders
    val storagePermsHolder: PermsHolder,
    val systemUIModeHolder: SystemUIModeHolder,
    val windowInsetsHolder: WindowInsetsHolder,
    val lifecycleEventsHolder: LifecycleEventsHolder,
    val displayShapeHolder: DisplayShapeHolder,

// apps & icon packs
    val installedAppsHolder: InstalledAppsHolder,
    val installedIconPacksHolder: InstalledIconPacksHolder,
    val iconPackCache: IconPackCache,
    val iconCache: IconCache,
    val mockExporter: MockExporter,

    // webview
    val consoleMessagesHolder: DevConsoleMessagesHolder,
    val bridgeServer: BridgeServer,
    val bridgeToJSInterface: BridgeToJSAPI,
    val jsToBridgeInterface: JSToBridgeAPI,
)
