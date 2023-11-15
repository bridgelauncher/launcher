package com.tored.bridgelauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

data class InstalledApp(
    val uid: Int,
    val packageName: String,
    val launchIntent: Intent,
    var label: String,
    val icon: Drawable,
)

class InstalledAppsStateHolder(
    private val _pm: PackageManager
)
{
    val installedApps = mutableListOf<InstalledApp>()

    fun loadInstalledApps()
    {
        installedApps.clear()

        val apps = _pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in apps)
        {
            val launchIntent = _pm.getLaunchIntentForPackage(app.packageName)
            if (launchIntent != null)
            {
                installedApps.add(
                    InstalledApp(
                        app.uid,
                        app.packageName,
                        launchIntent,
                        _pm.getApplicationLabel(app).toString(),
                        _pm.getApplicationIcon(app),
                    )
                )
            }
        }

        installedApps.sortBy { it.label }
    }
}
