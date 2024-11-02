package com.tored.bridgelauncher.services.apps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.tored.bridgelauncher.utils.q
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

private const val TAG = "InstalledApps"

sealed class InstalledAppListChangeEvent
{
    data class Added(val newApp: InstalledApp) : InstalledAppListChangeEvent()
    data class Removed(val packageName: String) : InstalledAppListChangeEvent()
    data class Changed(val oldApp: InstalledApp, val newApp: InstalledApp) : InstalledAppListChangeEvent()
}

class InstalledAppsHolder(
    private val _pm: PackageManager,
)
{
    private val _scope = CoroutineScope(Dispatchers.Default) + SupervisorJob()

    private val _initialLoadingFinished = mutableStateOf(false)
    val initialLoadingFinished = _initialLoadingFinished as State<Boolean>

    private val _packageNameToInstalledAppMap = mutableStateMapOf<String, InstalledApp>()
    val packageNameToInstalledAppMap = _packageNameToInstalledAppMap as Map<String, InstalledApp>;

    private val _appListChangeEventFlow = MutableSharedFlow<InstalledAppListChangeEvent>()
    val appListChangeEventFlow = _appListChangeEventFlow.asSharedFlow()


    // region handling map changes

    private fun addOrChangeInstalledApp(newApp: InstalledApp)
    {
        val oldApp = _packageNameToInstalledAppMap.putIfAbsent(newApp.packageName, newApp)
        if (oldApp == null)
            _appListChangeEventFlow.tryEmit(InstalledAppListChangeEvent.Added(newApp))
        else
            changeInstalledApp(newApp)
    }

    private fun changeInstalledApp(newApp: InstalledApp)
    {
        val oldApp = _packageNameToInstalledAppMap[newApp.packageName]

        if (oldApp == null)
        {
            Log.w(TAG, "${::changeInstalledApp.name}: Changed app ${q(newApp.packageName)} was not on the list of apps. Changed event was not emitted.")
            return
        }

        _appListChangeEventFlow.tryEmit(InstalledAppListChangeEvent.Changed(oldApp, newApp))
    }

    private fun removeInstalledApp(packageName: String)
    {
        val app = _packageNameToInstalledAppMap.remove(packageName)

        if (app == null) Log.w(TAG, "${::removeInstalledApp.name}: Removed app ${q(packageName)} was not on the list of apps.")

        _appListChangeEventFlow.tryEmit(InstalledAppListChangeEvent.Removed(packageName))
    }

    // endregion


    // region loading apps from the package manager

    private fun loadInstalledApps()
    {
        _packageNameToInstalledAppMap.clear()

        _pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .forEach { addFromAppInfoIfLaunchable(it) }
    }

    private fun addFromAppInfoIfLaunchable(app: ApplicationInfo): InstalledApp?
    {
        return _pm.getLaunchIntentForPackage(app.packageName)?.let { launchIntent ->
            val newApp = InstalledApp(
                app.uid,
                app.packageName,
                _pm.getApplicationLabel(app).toString(),
                launchIntent,
                _pm.getApplicationIcon(app),
            )

            addOrChangeInstalledApp(newApp)

            newApp
        }
    }

    // endregion


    // region receiving notifications from BridgeLauncherBroadcastReciever

    // these functions could theoretically be replaced with collecting an event flow from the BridgeLauncherBroadcastReceiver instead,
    // but I've opted to keep it simple, because for now the BLBR is the only component that will send those notifications,
    // because no other component is notified by the system about apps being added, changed and removed.

    fun notifyAppAdded(packageName: String) = notifyAppAddedOrChanged(packageName)
    fun notifyAppChanged(packageName: String) = notifyAppAddedOrChanged(packageName)

    private fun notifyAppAddedOrChanged(packageName: String)
    {
        val appInfo = _pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        addFromAppInfoIfLaunchable(appInfo)
    }

    fun notifyAppRemoved(packageName: String)
    {
        removeInstalledApp(packageName)
    }

    // endregion


    private fun initialLoad()
    {
        loadInstalledApps()
        _initialLoadingFinished.value = true
        Log.d(TAG, "${::initialLoad.name}: OK, _packageNameToInstalledAppMap.size = ${_packageNameToInstalledAppMap.size}")
    }

    fun launchInitalLoad() = _scope.launch { initialLoad() }

}
