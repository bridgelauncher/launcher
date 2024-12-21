package com.tored.bridgelauncher.services.iconpacks2.list

import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.tored.bridgelauncher.services.apps.loadInstalledApp
import com.tored.bridgelauncher.services.apps.tryLoadInstalledAppIfLaunchable
import com.tored.bridgelauncher.services.iconpacks2.list.events.IIconPackListChangeEvent
import com.tored.bridgelauncher.services.pkgevents.PackageEventsHolder
import com.tored.bridgelauncher.utils.canAppHandleIntent
import com.tored.bridgelauncher.utils.getValueAndUseDuration
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

val iconPackIntentActions = arrayOf(
    "com.fede.launcher.THEME_ICONPACK",
    "com.anddoes.launcher.THEME",
    "com.novalauncher.THEME",
    "com.teslacoilsw.launcher.THEME",
    "com.gau.go.launcherex.theme",
    "org.adw.launcher.THEMES",
    "org.adw.launcher.icons.ACTION_PICK_ICON",
)

private val TAG = "InstalledIconPacks"

class InstalledIconPacksHolder(
    private val _pm: PackageManager,
    private val _packageEventsHolder: PackageEventsHolder,
)
{
    private val _scope = CoroutineScope(Dispatchers.Main)

    private val _packageNameToIconPackMap = MutableStateFlow<Map<String, IconPackInfo>?>(null)
    val packageNameToIconPackMap = _packageNameToIconPackMap.asStateFlow()

    // events

    private val _iconPackAdded = MutableSharedFlow<IIconPackListChangeEvent.Added>(extraBufferCapacity = 1)
    val iconPackAdded = _iconPackAdded.asSharedFlow()

    private val _iconPackChanged = MutableSharedFlow<IIconPackListChangeEvent.Changed>(extraBufferCapacity = 1)
    val iconPackChanged = _iconPackChanged.asSharedFlow()

    private val _iconPackRemoved = MutableSharedFlow<IIconPackListChangeEvent.Removed>(extraBufferCapacity = 1)
    val iconPackRemoved = _iconPackRemoved.asSharedFlow()


    private suspend fun initialLoad()
    {
        _packageNameToIconPackMap.value = loadIconPacks()
    }

    private suspend fun loadIconPacks(): Map<String, IconPackInfo>
    {
        val map = mutableMapOf<String, IconPackInfo>()

        for (action in iconPackIntentActions)
        {
            val intent = Intent(action)
            val resolveInfos = getValueAndUseDuration(
                get = { _pm.queryIntentActivities(intent, PackageManager.GET_META_DATA) },
                run = { Log.d(TAG, "loadIconPacks/$action: queryIntentActivities took ${it.inWholeMilliseconds}ms") }
            )

            measureTimeMillis {
                coroutineScope {
                    resolveInfos.forEach { ri ->
                        val appInfo = ri.activityInfo.applicationInfo
                        if (!map.containsKey(appInfo.packageName))
                        {
                            launch {
                                val app = _pm.loadInstalledApp(appInfo)

                                map[appInfo.packageName] = IconPackInfo(
                                    app = app
                                )

                                // TODO: preload a cache entry for this icon pack
                            }
                        }
                    }
                }
            }.let { ms ->
                Log.d(TAG, "loadIconPacks/$action: forEach took ${ms}ms")
            }
        }

        Log.d(TAG, "loadIconPacks: OK, icon pack count: ${map.size}")
        return map
    }

    /** Waits until the first non-null value appears in the [_packageNameToIconPackMap] (which happens when [initialLoad] finishes). */
    private suspend fun waitForInitialLoad()
    {
        _packageNameToIconPackMap.first { it != null }
    }

    /** Checks if the app with the given [packageName] claims to be an icon pack. */
    private suspend fun isAppAnIconPack(packageName: String): Boolean
    {
        var isIconPack = false

        // check each action in parallel and exit as soon as one match is found
        try
        {
            coroutineScope {
                for (action in iconPackIntentActions)
                {
                    launch {
                        val intent = Intent(action).apply {
                            setPackage(packageName)
                        }

                        if (_pm.canAppHandleIntent(packageName, intent))
                        {
                            isIconPack = true
                            cancel()
                        }
                    }
                }
            }
        }
        catch (_: CancellationException)
        {
        }

        return isIconPack
    }

    private suspend fun handlePackageAddedOrReplaced(packageName: String)
    {
        waitForInitialLoad()

        _packageNameToIconPackMap.update { oldMap ->
            (oldMap?.toMutableMap() ?: mutableMapOf()).also { newMap ->
                when (val app = _pm.tryLoadInstalledAppIfLaunchable(packageName))
                {
                    // app might have been mutated to no longer be an icon pack
                    null ->
                    {
                        newMap.remove(packageName)?.also {
                            _iconPackRemoved.tryEmit(IIconPackListChangeEvent.Removed(it))
                        }
                    }

                    else ->
                    {
                        if (isAppAnIconPack(packageName))
                        {
                            val oldIconPack = newMap[packageName]

                            val newIconPack = IconPackInfo(
                                app = app
                            )

                            newMap[packageName] = newIconPack

                            if (oldIconPack == null)
                                _iconPackAdded.tryEmit(IIconPackListChangeEvent.Added(newIconPack))
                            else
                                _iconPackChanged.tryEmit(IIconPackListChangeEvent.Changed(oldIconPack, newIconPack))
                        }

                    }
                }
            }
        }
    }

    private suspend fun removeIconPack(packageName: String)
    {
        waitForInitialLoad()

        _packageNameToIconPackMap.update { oldMap ->
            (oldMap?.toMutableMap() ?: mutableMapOf()).also { newMap ->
                newMap.remove(packageName)?.also {
                    _iconPackRemoved.tryEmit(IIconPackListChangeEvent.Removed(it))
                }
            }
        }
    }


    fun startup()
    {
        _scope.launch { initialLoad() }
        _scope.launch { _packageEventsHolder.packageAddedEvents.collect { handlePackageAddedOrReplaced(it.packageName) } }
        _scope.launch { _packageEventsHolder.packageReplacedEvents.collect { handlePackageAddedOrReplaced(it.packageName) } }
        _scope.launch { _packageEventsHolder.packageRemovedEvents.collect { removeIconPack(it.packageName) } }
    }
}