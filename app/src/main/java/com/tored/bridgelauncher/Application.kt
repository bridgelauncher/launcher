package com.tored.bridgelauncher

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BridgeLauncherApp : Application()
{
    lateinit var installedAppsHolder: InstalledAppsStateHolder
    lateinit var consoleMessagesHolder: ConsoleMessagesHolder

    override fun onCreate()
    {
        super.onCreate()

        installedAppsHolder = InstalledAppsStateHolder(this.packageManager).apply()
        {
            loadInstalledApps()
        }

        consoleMessagesHolder = ConsoleMessagesHolder()
    }
}