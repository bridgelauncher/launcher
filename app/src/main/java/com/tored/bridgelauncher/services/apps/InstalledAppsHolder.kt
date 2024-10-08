package com.tored.bridgelauncher.services.apps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf

private const val TAG = "InstalledApps"

typealias AppAddedOrChangedEventListener = (app: InstalledApp) -> Unit;
typealias AppRemovedEventListener = (packageName: String) -> Unit;

class InstalledAppsHolder(
    private val _pm: PackageManager,
)
{
    val installedApps = mutableMapOf<String, InstalledApp>()
    val apps = mutableStateMapOf<String, InstalledApp>()

    // TODO: lazy load
    fun loadInstalledApps()
    {
        installedApps.clear()

        _pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .forEach { setAppFromAppInfo(it) }
    }

    private fun setAppFromAppInfo(app: ApplicationInfo): InstalledApp?
    {
        val launchIntent = _pm.getLaunchIntentForPackage(app.packageName)
        if (launchIntent != null)
        {
            val newApp = InstalledApp(
                app.uid,
                app.packageName,
                _pm.getApplicationLabel(app).toString(),
                launchIntent,
                _pm.getApplicationIcon(app),
            )

            installedApps[app.packageName] = newApp

            return newApp
        }
        else
        {
            return null
        }
    }

    private fun updateAppInfo(packageName: String): InstalledApp?
    {
        return try
        {
            val app = _pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            setAppFromAppInfo(app)
        }
        catch (ex: Exception)
        {
            Log.e(TAG, "updateAppInfo: $packageName", ex)
            null
        }
    }


    // change notifications

    private val _addListeners = mutableSetOf<AppAddedOrChangedEventListener>()
    private val _changeListeners = mutableSetOf<AppAddedOrChangedEventListener>()
    private val _removeListeners = mutableSetOf<AppRemovedEventListener>()

    fun onAdded(listener: AppAddedOrChangedEventListener) = _addListeners.add(listener)
    fun onChanged(listener: AppAddedOrChangedEventListener) = _changeListeners.add(listener)
    fun onRemoved(listener: AppRemovedEventListener) = _removeListeners.add(listener)

    fun notifyAppAdded(packageName: String)
    {
        val app = updateAppInfo(packageName)

        if (app != null)
            _addListeners.forEach { it(app) }
    }

    fun notifyAppChanged(packageName: String)
    {
        val app = updateAppInfo(packageName)
        if (app != null)
            _changeListeners.forEach { it(app) }
    }

    fun notifyAppRemoved(packageName: String)
    {
        installedApps.remove(packageName)
        _removeListeners.forEach { it(packageName) }
    }
}
