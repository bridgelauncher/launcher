package com.tored.bridgelauncher.services

import android.content.BroadcastReceiver
import android.content.pm.PackageManager
import com.tored.bridgelauncher.api2.bridgetojs.BridgeToJSInterface
import com.tored.bridgelauncher.api2.jstobridge.JSToBridgeInterface
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
import com.tored.bridgelauncher.services.windowinsetsholder.WindowInsetsHolder

data class BridgeServices(
    val packageManager: PackageManager,
    val storagePermsManager: PermsHolder,

    val installedAppsHolder: InstalledAppsHolder,
    val installedIconPacksHolder: InstalledIconPacksHolder,
    val iconPackCache: IconPackCache,
    val iconCache: IconCache,

    val bridgeServer: BridgeServer,
    val consoleMessagesHolder: DevConsoleMessagesHolder,
    val broadcastReceiver: BroadcastReceiver,
    val bridgeToJSInterface: BridgeToJSInterface,
    val jsToBridgeInterface: JSToBridgeInterface,
    val windowInsetsHolder: WindowInsetsHolder,
    val lifecycleEventsHolder: LifecycleEventsHolder,
    val displayShapeHolder: DisplayShapeHolder,

    val mockExporter: MockExporter,
)
