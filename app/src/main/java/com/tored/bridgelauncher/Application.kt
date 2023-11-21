package com.tored.bridgelauncher

import android.app.Application
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
//        startService(Intent(this, BridgeHttpServerService::class.java))
    }
}