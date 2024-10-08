package com.tored.bridgelauncher.services.iconpacks

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

const val ACTION_ADW_LAUNCHER_THEME = "org.adw.launcher.THEMES"
const val ACTION_GO_LAUNCHER_THEME = "com.gau.go.launcherex.theme"

class InstalledIconPacksHolder(
    private val _pm: PackageManager
)
{
    private var _iconPacksCache: MutableMap<String, InstalledIconPackHolder>? = null

    private val _iconPacksLoadingMutex = Mutex()
    suspend fun getIconPacks(): Map<String, InstalledIconPackHolder>
    {
        // return immediately if icon packs are already loaded and cached
        return _iconPacksCache ?: kotlin.run()
        {
            // mutex ensures the first thread to get here loads the packs, the rest wait for the lock to be released
            // when another thread enters here, the packs will already be loaded and thus it'll exit immediately
            _iconPacksLoadingMutex.withLock()
            {
                if (_iconPacksCache == null)
                    _iconPacksCache = loadIconPacks()
            }

            // we're past the mutex which must mean the packs map is loaded
            return _iconPacksCache!!
        }
    }

    private suspend fun loadIconPacks() = coroutineScope()
    {
        val iconPacks = mutableMapOf<String, InstalledIconPackHolder>()

        launch {
            _pm.queryIntentActivities(Intent(ACTION_ADW_LAUNCHER_THEME), PackageManager.GET_META_DATA)
                .forEach { addNewIconPack(iconPacks, it) }
        }

        launch {
            _pm.queryIntentActivities(Intent(ACTION_GO_LAUNCHER_THEME), PackageManager.GET_META_DATA)
                .forEach { addNewIconPack(iconPacks, it) }
        }

        iconPacks
    }

    /** Adds an icon pack from a ResolveInfo, but only if it hasn't been added yet. */
    private fun addNewIconPack(iconPacks: MutableMap<String, InstalledIconPackHolder>, info: ResolveInfo)
    {
        if (!iconPacks.containsKey(info.activityInfo.packageName))
            iconPacks[info.activityInfo.packageName] = InstalledIconPackHolder(_pm, info.activityInfo.packageName)
    }

    // TODO: react on app installed/changed/removed
}
