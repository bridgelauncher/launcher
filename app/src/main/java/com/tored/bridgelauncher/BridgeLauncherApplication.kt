package com.tored.bridgelauncher

import android.app.Application
import android.app.UiModeManager
import android.content.ComponentName
import android.util.Log
import androidx.core.content.ContextCompat
import com.tored.bridgelauncher.api2.bridgetojs.BridgeToJSAPI
import com.tored.bridgelauncher.api2.jstobridge.JSToBridgeAPI
import com.tored.bridgelauncher.api2.server.BridgeServer
import com.tored.bridgelauncher.services.BridgeServices
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.devconsole.DevConsoleMessagesHolder
import com.tored.bridgelauncher.services.displayshape.DisplayShapeHolder
import com.tored.bridgelauncher.services.iconcache.IconCache
import com.tored.bridgelauncher.services.iconpackcache.IconPackCache
import com.tored.bridgelauncher.services.iconpackcache.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.lifecycleevents.LifecycleEventsHolder
import com.tored.bridgelauncher.services.mockexport.MockExporter
import com.tored.bridgelauncher.services.perms.PermsHolder
import com.tored.bridgelauncher.services.system.BridgeButtonQSTileService
import com.tored.bridgelauncher.services.system.BridgeLauncherBroadcastReceiver
import com.tored.bridgelauncher.services.system.BridgeLauncherDeviceAdminReceiver
import com.tored.bridgelauncher.services.uimode.SystemUIModeHolder
import com.tored.bridgelauncher.services.windowinsetsholder.WindowInsetsHolder

private const val TAG = "Application"

class BridgeLauncherApplication : Application()
{
    lateinit var adminReceiverComponentName: ComponentName
    lateinit var qsTileServiceComponentName: ComponentName

    lateinit var services: BridgeServices

    override fun onCreate()
    {
        super.onCreate()
        Log.d(TAG, "super.onCreate(): OK")

        services = createServices()
        Log.d(TAG, "createServices(): OK")

        startup()
        Log.d(TAG, "startup(): OK")
    }

    private fun createServices(): BridgeServices
    {
        adminReceiverComponentName = ComponentName(this, BridgeLauncherDeviceAdminReceiver::class.java)
        qsTileServiceComponentName = ComponentName(this, BridgeButtonQSTileService::class.java)

        // this is deliberately set up like this so that whenver a service is added to the provider, the constructor below will complain
        // constructing the services ahead of time helps with manually resolving the dependency graph at compile time
        // yeah this probably could be done by some DI library but I'd rather explicitly know what is happening

        val pm = packageManager
        val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager

        val permsHolder = PermsHolder(this)

        val installedAppsHolder = InstalledAppsHolder(pm)
        val iconPackCache = IconPackCache()
        val appIconsCache = IconCache(pm, installedAppsHolder, iconPackCache)
        val installedIconPacksHolder = InstalledIconPacksHolder(
            _pm = pm,
            _apps = installedAppsHolder
        )

        val lifecycleEventsHolder = LifecycleEventsHolder()
        val windowInsetsHolder = WindowInsetsHolder()
        val displayShapeHolder = DisplayShapeHolder()
        val systemUIModeHolder = SystemUIModeHolder(
            _uiModeManager = uiModeManager
        )

        val bridgeToJSAPI = BridgeToJSAPI(
            _app = this,
            _perms = permsHolder,
            _insets = windowInsetsHolder,
            _lifecycleEventsHolder = lifecycleEventsHolder,
            _apps = installedAppsHolder,
            _systemUIMode = systemUIModeHolder,
        )

        val jsToBridgeAPI = JSToBridgeAPI(
            _app = this,
            _windowInsetsHolder = windowInsetsHolder,
            _displayShapeHolder = displayShapeHolder,
        )

        val bridgeServer = BridgeServer(
            this,
            installedAppsHolder,
            _iconPacks = installedIconPacksHolder,
        )

        val consoleMessagesHolder = DevConsoleMessagesHolder()


        val mockExporter = MockExporter(
            installedAppsHolder,
            installedIconPacksHolder
        )

        val broadcastReceiver = BridgeLauncherBroadcastReceiver(installedAppsHolder)

        return BridgeServices(
            // system
            packageManager = pm,
            uiModeManager = uiModeManager,
            broadcastReceiver = broadcastReceiver,

            // state holders
            storagePermsHolder = permsHolder,
            systemUIModeHolder = systemUIModeHolder,
            windowInsetsHolder = windowInsetsHolder,
            lifecycleEventsHolder = lifecycleEventsHolder,
            displayShapeHolder = displayShapeHolder,

            // apps & icon packs
            installedAppsHolder = installedAppsHolder,
            installedIconPacksHolder = installedIconPacksHolder,
            iconPackCache = iconPackCache,
            iconCache = appIconsCache,
            mockExporter = mockExporter,

            // webview
            consoleMessagesHolder = consoleMessagesHolder,
            bridgeServer = bridgeServer,
            bridgeToJSInterface = bridgeToJSAPI,
            jsToBridgeInterface = jsToBridgeAPI,
        )
    }

    private fun startup()
    {
        ContextCompat.registerReceiver(
            this,
            services.broadcastReceiver,
            BridgeLauncherBroadcastReceiver.intentFilter,
            ContextCompat.RECEIVER_EXPORTED,
        )

        services.iconPackCache.startup()
        services.installedIconPacksHolder.startup()
        services.iconCache.startup()
        services.installedAppsHolder.startup()
        services.bridgeToJSInterface.startup()
    }
}