package com.tored.bridgelauncher.services.iconpackcache

import android.content.Intent
import android.content.pm.PackageManager
import com.tored.bridgelauncher.services.apps.InstalledAppListChangeEvent
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

val ICON_PACK_INTENT_ACTIONS = arrayOf(
    "com.fede.launcher.THEME_ICONPACK",
    "com.anddoes.launcher.THEME",
    "com.novalauncher.THEME",
    "com.teslacoilsw.launcher.THEME",
    "com.gau.go.launcherex.theme",
    "org.adw.launcher.THEMES",
    "org.adw.launcher.icons.ACTION_PICK_ICON",
)

class InstalledIconPacksHolder(
    private val _pm: PackageManager,
    private val _apps: InstalledAppsHolder,
)
{
    private val _coroutineScope = CoroutineScope(Dispatchers.Default) + SupervisorJob()

    private fun loadIconPack(packageName: String)
    {

    }

    private fun removeIconPack(packageName: String)
    {

    }

    private fun initialLoad()
    {
        runBlocking {
            supervisorScope {
                ICON_PACK_INTENT_ACTIONS.forEach { action ->
                    launch {
                        val intent = Intent(action)
                        val resolveInfos = _pm.queryIntentActivities(intent, PackageManager.GET_META_DATA)
                        resolveInfos.forEach {ri ->
                            launch {
                                loadIconPack(ri.activityInfo.packageName)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startCollectingAppListChangeEvents()
    {
        _coroutineScope.launch {
            _apps.appListChangeEventFlow.collect { event ->
                when (event)
                {
                    is InstalledAppListChangeEvent.Added -> loadIconPack(event.newApp.packageName)
                    is InstalledAppListChangeEvent.Changed -> loadIconPack(event.newApp.packageName)
                    is InstalledAppListChangeEvent.Removed -> removeIconPack(event.packageName)
                }
            }
        }
    }
}