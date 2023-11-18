package com.tored.bridgelauncher

import android.app.Application
import android.content.Intent
import com.tored.bridgelauncher.httpserver.BridgeHttpServerService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BridgeLauncherApp : Application()
{
    lateinit var installedAppsHolder: InstalledAppsStateHolder

    override fun onCreate()
    {
        super.onCreate()

        installedAppsHolder = InstalledAppsStateHolder(this.packageManager).apply()
        {
            loadInstalledApps()
        }

        // start HTTP server
        startService(Intent(this, BridgeHttpServerService::class.java))
    }
}