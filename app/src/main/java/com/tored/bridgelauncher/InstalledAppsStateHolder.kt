package com.tored.bridgelauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import java.text.Normalizer

data class InstalledApp(
    val uid: Int,
    val packageName: String,
    val launchIntent: Intent,
    var label: String,
    val icon: Drawable,
)
{
    val labelSimplified = simplifyLabel(label)

    companion object {
        fun simplifyLabel(label: String): String
        {
            return Normalizer
                .normalize(label.trim(), Normalizer.Form.NFD)
                .replace(Regex("\\p{Mn}+"), "")
                .lowercase()
                .replace(Regex("[^a-z0-9]"), "")
        }
    }
}

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
