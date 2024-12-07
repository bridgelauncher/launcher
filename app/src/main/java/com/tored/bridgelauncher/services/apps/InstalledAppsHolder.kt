package com.tored.bridgelauncher.services.apps

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import com.tored.bridgelauncher.utils.q
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis
import kotlin.time.measureTimedValue

private const val TAG = "InstalledApps"

sealed interface InstalledAppListChangeEvent
{
    data class Added(
        val newApp: InstalledApp,
        val isFromInitialLoad: Boolean
    ) : InstalledAppListChangeEvent
    data class Removed(val packageName: String) : InstalledAppListChangeEvent
    data class Changed(val oldApp: InstalledApp, val newApp: InstalledApp) : InstalledAppListChangeEvent
}

class InstalledAppsHolder(
    private val _pm: PackageManager,
)
{
    private val _coroutineScope = CoroutineScope(Dispatchers.Default) + SupervisorJob()

    private val _initialLoadingFinished = MutableStateFlow(false)
    val initialLoadingFinished = _initialLoadingFinished.asStateFlow()

    private val _packageNameToInstalledAppMap = mutableStateMapOf<String, InstalledApp>()
    val packageNameToInstalledAppMap = _packageNameToInstalledAppMap as Map<String, InstalledApp>

    // if extraBufferCapacity is not set to 1, tryEmit simply doesn't work
    private val _appListChangeEventFlow = MutableSharedFlow<InstalledAppListChangeEvent>(extraBufferCapacity = 1)
    val appListChangeEventFlow = _appListChangeEventFlow.asSharedFlow()


    // region handling map changes

    private fun addOrChangeInstalledApp(
        newApp: InstalledApp,
        isFromInitialLoad: Boolean,
    )
    {
        val oldApp = _packageNameToInstalledAppMap.putIfAbsent(newApp.packageName, newApp)
        if (oldApp == null)
        {
            val result = _appListChangeEventFlow.tryEmit(InstalledAppListChangeEvent.Added(newApp, isFromInitialLoad))
            Log.d(TAG, "addOrChangeInstalledApp: $result")
        }
        else
            changeInstalledApp(newApp)
    }

    private fun changeInstalledApp(newApp: InstalledApp)
    {
        val oldApp = _packageNameToInstalledAppMap[newApp.packageName]

        if (oldApp == null)
        {
            Log.w(TAG, "${::changeInstalledApp.name}: Changed app ${q(newApp.packageName)} was not on the list of apps. Changed event not emitted.")
        }
        else
        {
            val result = _appListChangeEventFlow.tryEmit(InstalledAppListChangeEvent.Changed(oldApp, newApp))
            Log.d(TAG, "changeInstalledApp: $result")
        }
    }

    private fun removeInstalledApp(packageName: String)
    {
        val app = _packageNameToInstalledAppMap.remove(packageName)

        if (app == null) Log.w(TAG, "${::removeInstalledApp.name}: Removed app ${q(packageName)} was not on the list of apps.")

        val result = _appListChangeEventFlow.tryEmit(InstalledAppListChangeEvent.Removed(packageName))
        Log.d(TAG, "removeInstalledApp: $result")
    }

    // endregion


    // region loading apps from the package manager

    private fun loadInstalledApps()
    {
        _packageNameToInstalledAppMap.clear()

        val appInfos = measureTimedValue {
            _pm.getInstalledApplications(PackageManager.GET_META_DATA)
        }.let { (appInfos, dur) ->
            Log.d(TAG, "loadInstalledApps: getInstalledApplications took ${dur.inWholeMilliseconds}ms")
            appInfos
        }

        measureTimeMillis {
            runBlocking {
                coroutineScope {
                    appInfos.forEach {
                        launch { addFromAppInfoIfLaunchable(it, isFromInitialLoad = true) }
                    }
                }
            }
        }.let { ms ->
            Log.d(TAG, "loadInstalledApps: forEach took ${ms}ms")
        }


    }

    private fun addFromAppInfoIfLaunchable(
        appInfo: ApplicationInfo,
        isFromInitialLoad: Boolean,
    ): InstalledApp?
    {
        return _pm.getLaunchIntentForPackage(appInfo.packageName)?.let { launchIntent ->
            val newApp = InstalledApp(
                appInfo.uid,
                appInfo.packageName,
                _pm.getApplicationLabel(appInfo).toString(),
                launchIntent,
                _pm.getApplicationIcon(appInfo),
                appInfo,
            )

            addOrChangeInstalledApp(
                newApp,
                isFromInitialLoad,
            )

            newApp
        }
    }

    // endregion


    // region receiving notifications from BridgeLauncherBroadcastReciever

    // these functions could theoretically be replaced with collecting an event flow from the BridgeLauncherBroadcastReceiver (BLBR) instead,
    // but I've opted to keep it simple, because for now the BLBR is the only component that will send those notifications,
    // because no other component is notified by the system about apps being added, changed and removed.

    fun notifyAppAdded(packageName: String) = notifyAppAddedOrChanged(packageName)
    fun notifyAppChanged(packageName: String) = notifyAppAddedOrChanged(packageName)

    private fun notifyAppAddedOrChanged(packageName: String)
    {
        val appInfo = _pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        addFromAppInfoIfLaunchable(appInfo, isFromInitialLoad = false)
    }

    fun notifyAppRemoved(packageName: String)
    {
        removeInstalledApp(packageName)
    }

    // endregion


    private fun initialLoad()
    {
        measureTimeMillis {
            loadInstalledApps()
            _initialLoadingFinished.value = true
        }.let { ms ->
            Log.d(TAG, "${::initialLoad.name}: OK in ${ms}ms, _packageNameToInstalledAppMap.size = ${_packageNameToInstalledAppMap.size}")
        }
    }

    private fun launchInitalLoad() = _coroutineScope.launch { initialLoad() }

    fun startup()
    {
        launchInitalLoad()
    }

}
