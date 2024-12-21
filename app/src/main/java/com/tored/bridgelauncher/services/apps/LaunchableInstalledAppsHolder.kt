package com.tored.bridgelauncher.services.apps

import android.content.pm.PackageManager
import android.util.Log
import com.tored.bridgelauncher.services.pkgevents.PackageEventsHolder
import com.tored.bridgelauncher.utils.getValueAndUseDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

private const val TAG = "InstalledApps"

/** Loads and caches information about launchable installed apps. */
class LaunchableInstalledAppsHolder(
    private val _pm: PackageManager,
    private val _packageEventsHolder: PackageEventsHolder,
)
{
    private val _scope = CoroutineScope(Dispatchers.Main)

    private val _packageNameToInstalledAppMap = MutableStateFlow<Map<String, InstalledApp>?>(null)
    val packageNameToInstalledAppMap = _packageNameToInstalledAppMap.asStateFlow()


    private val _appAddedEvents = MutableSharedFlow<AppListChangeEvent.Added>(extraBufferCapacity = 1)
    val appAddedEvents = _appAddedEvents.asSharedFlow()

    private val _appChangedEvents = MutableSharedFlow<AppListChangeEvent.Changed>(extraBufferCapacity = 1)
    val appChangedEvents = _appChangedEvents.asSharedFlow()

    private val _appRemovedEvents = MutableSharedFlow<AppListChangeEvent.Removed>(extraBufferCapacity = 1)
    val appRemovedEvents = _appRemovedEvents.asSharedFlow()


    private suspend fun initialLoad()
    {
        _packageNameToInstalledAppMap.value = loadLaunchableInstalledApps()
    }

    /** Loads a map of packageName -> launchable installed app in parallel. */
    private suspend fun loadLaunchableInstalledApps(): Map<String, InstalledApp>
    {
        val map = mutableMapOf<String, InstalledApp>()

        val appInfos = getValueAndUseDuration(
            get = { _pm.getInstalledApplications(PackageManager.GET_META_DATA) },
            run = { Log.d(TAG, "loadInstalledApps: getInstalledApplications took ${it.inWholeMilliseconds}ms") }
        )

        measureTimeMillis {
            coroutineScope {
                appInfos.forEach { appInfo ->
                    launch {
                        _pm.tryLoadInstalledAppIfLaunchable(appInfo)?.let {
                            map[it.packageName] = it
                        }
                    }
                }
            }
        }.let { ms ->
            Log.d(TAG, "loadInstalledApps: forEach took ${ms}ms")
        }

        Log.d(TAG, "loadInstalledApps: OK, app count: ${map.size}")
        return map
    }

    /** Waits until the first non-null value appears in the [_packageNameToInstalledAppMap] (which happens when [initialLoad] finishes). */
    private suspend fun waitForInitialLoad()
    {
        _packageNameToInstalledAppMap.first { it != null }
    }

    private suspend fun addOrReloadApp(packageName: String)
    {
        waitForInitialLoad()

        _packageNameToInstalledAppMap.update { oldMap ->
            (oldMap?.toMutableMap() ?: mutableMapOf()).also { newMap ->
                when (val newApp = _pm.tryLoadInstalledAppIfLaunchable(packageName))
                {
                    // app might have been mutated to no longer be launchable
                    null ->
                    {
                        newMap.remove(packageName)?.also { oldApp ->
                            _appRemovedEvents.tryEmit(AppListChangeEvent.Removed(oldApp))
                        }
                    }

                    else ->
                    {
                        val oldApp = newMap[packageName]
                        newMap[packageName] = newApp
                        if (oldApp == null)
                            _appAddedEvents.tryEmit(AppListChangeEvent.Added(newApp))
                        else
                            _appChangedEvents.tryEmit(AppListChangeEvent.Changed(oldApp, newApp))
                    }
                }
            }
        }
    }

    private suspend fun removeApp(packageName: String)
    {
        waitForInitialLoad()

        _packageNameToInstalledAppMap.update { oldMap ->
            (oldMap?.toMutableMap() ?: mutableMapOf()).also { newMap ->
                newMap.remove(packageName)?.also { oldApp ->
                    _appRemovedEvents.tryEmit(AppListChangeEvent.Removed(oldApp))
                }
            }
        }
    }

    fun startup()
    {
        // order here doesn't matter, as addOrReloadApp and removeApp wait for the _packageNameToInstalledAppMap to not be null,
        _scope.launch { initialLoad() }
        _scope.launch { _packageEventsHolder.packageAddedEvents.collect { addOrReloadApp(it.packageName) } }
        _scope.launch { _packageEventsHolder.packageReplacedEvents.collect { addOrReloadApp(it.packageName) } }
        _scope.launch { _packageEventsHolder.packageRemovedEvents.collect { removeApp(it.packageName) } }
    }

}

