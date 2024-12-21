package com.tored.bridgelauncher.services.apps

sealed interface AppListChangeEvent
{
    data class Added(val newApp: InstalledApp) : AppListChangeEvent
    data class Changed(val oldApp: InstalledApp, val newApp: InstalledApp) : AppListChangeEvent
    data class Removed(val oldApp: InstalledApp) : AppListChangeEvent
}