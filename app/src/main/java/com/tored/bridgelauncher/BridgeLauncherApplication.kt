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
import com.tored.bridgelauncher.services.apps.LaunchableInstalledAppsHolder
import com.tored.bridgelauncher.services.devconsole.DevConsoleMessagesHolder
import com.tored.bridgelauncher.services.displayshape.DisplayShapeHolder
import com.tored.bridgelauncher.services.iconcache.IconCache
import com.tored.bridgelauncher.services.iconpacks2.appfilter.parser.AppFilterXMLParser
import com.tored.bridgelauncher.services.iconpacks2.cache.IconPackCache
import com.tored.bridgelauncher.services.iconpacks2.list.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.lifecycleevents.LifecycleEventsHolder
import com.tored.bridgelauncher.services.mockexport.MockExporter
import com.tored.bridgelauncher.services.perms.PermsHolder
import com.tored.bridgelauncher.services.pkgevents.PackageEventsHolder
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

        val lifecycleEventsHolder = LifecycleEventsHolder()
        val packageEventsHolder = PackageEventsHolder()

        val permsHolder = PermsHolder(this)

        val appFilterXMLParser = AppFilterXMLParser(
            _pm = pm
        )

        val installedAppsHolder = LaunchableInstalledAppsHolder(
            _pm = pm,
            _packageEventsHolder = packageEventsHolder,
        )

        val installedIconPacksHolder = InstalledIconPacksHolder(
            _pm = pm,
            _packageEventsHolder = packageEventsHolder,
        )

        val iconPackCache = IconPackCache(
            _appFilterXMLParser = appFilterXMLParser,
            _installedIconPacks = installedIconPacksHolder,
        )

        val appIconsCache = IconCache(
            _pm = pm,
            _apps = installedAppsHolder,
            _iconPackCache = iconPackCache,
            _packageEventsHolder = packageEventsHolder,
        )

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
            _app = this,
            _apps = installedAppsHolder,
            _iconPacks = installedIconPacksHolder,
            _iconCache = appIconsCache,
        )

        val consoleMessagesHolder = DevConsoleMessagesHolder()


        val mockExporter = MockExporter(
            _apps = installedAppsHolder,
            _iconPacks = installedIconPacksHolder,
            _iconCache = appIconsCache,
            _iconPackCache = iconPackCache,
        )

        val broadcastReceiver = BridgeLauncherBroadcastReceiver(
            _packageEventsHolder = packageEventsHolder,
        )

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
            packageEventsHolder = packageEventsHolder,
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

        services.installedIconPacksHolder.startup()
        services.iconPackCache.startup()
        services.iconCache.startup()
        services.installedAppsHolder.startup()
        services.bridgeToJSInterface.startup()
    }
}