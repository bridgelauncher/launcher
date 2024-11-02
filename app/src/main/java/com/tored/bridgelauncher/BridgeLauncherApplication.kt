package com.tored.bridgelauncher

import android.app.Application
import android.content.ComponentName
import android.util.Log
import androidx.core.content.ContextCompat
import com.tored.bridgelauncher.api.jsapi.BridgeToJSAPI
import com.tored.bridgelauncher.api.jsapi.JSToBridgeAPI
import com.tored.bridgelauncher.api.server.BridgeServer
import com.tored.bridgelauncher.services.BridgeServices
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.iconpacks.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.mockexport.MockExporter
import com.tored.bridgelauncher.services.perms.PermsManager
import com.tored.bridgelauncher.services.settings.SettingsVM
import com.tored.bridgelauncher.services.system.BridgeButtonQSTileService
import com.tored.bridgelauncher.services.system.BridgeLauncherBroadcastReceiver
import com.tored.bridgelauncher.services.system.BridgeLauncherDeviceAdminReceiver
import com.tored.bridgelauncher.utils.checkCanSetSystemNightMode
import kotlinx.coroutines.MainScope

private const val TAG = "BridgeLauncherApplication"

class BridgeLauncherApplication : Application()
{
    lateinit var adminReceiverComponentName: ComponentName
    lateinit var qsTileServiceComponentName: ComponentName

    lateinit var services: BridgeServices

    lateinit var consoleMessagesHolder: ConsoleMessagesHolder

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

        val singletonCoroutineScope = MainScope()

        val pm = packageManager

        val storagePermsManager = PermsManager(this)
        val settingsVM = SettingsVM(this)

        val installedAppsHolder = InstalledAppsHolder(pm)
        val installedIconPacksHolder = InstalledIconPacksHolder(pm)

        val bridgeToJSAPI = BridgeToJSAPI(
            this,
            settingsVM,
            installedAppsHolder,
            checkCanSetSystemNightMode()
        )

        val jsToBridgeAPI = JSToBridgeAPI(
            this,
            singletonCoroutineScope,
            settingsVM,
            null
        )

        val bridgeServer = BridgeServer(
            settingsVM,
            installedAppsHolder,
            installedIconPacksHolder,
        )

        val consoleMessagesHolder = ConsoleMessagesHolder()

        val mockExporter = MockExporter(
            installedAppsHolder,
            installedIconPacksHolder
        )

        val broadcastReceiver = BridgeLauncherBroadcastReceiver(installedAppsHolder)

        return BridgeServices(
            packageManager = pm,
            settingsVM = settingsVM,
            storagePermsManager = storagePermsManager,

            installedAppsHolder = installedAppsHolder,
            installedIconPacksHolder = installedIconPacksHolder,

            bridgeServer = bridgeServer,
            consoleMessagesHolder = consoleMessagesHolder,
            broadcastReceiver = broadcastReceiver,
            bridgeToJSAPI = bridgeToJSAPI,
            jsToBridgeAPI = jsToBridgeAPI,

            mockExporter = mockExporter,
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

        services.installedAppsHolder.launchInitalLoad()
    }
}