package com.tored.bridgelauncher.services.iconcache

import android.content.pm.PackageManager
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.tored.bridgelauncher.services.apps.LaunchableInstalledAppsHolder
import com.tored.bridgelauncher.services.iconpacks2.cache.IconPackCache
import com.tored.bridgelauncher.services.pkgevents.PackageEventsHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class IconCache(
    private val _pm: PackageManager,
    private val _packageEventsHolder: PackageEventsHolder,
    private val _apps: LaunchableInstalledAppsHolder,
    private val _iconPackCache: IconPackCache,
)
{
    private val _scope = CoroutineScope(Dispatchers.Main)

    private val _cache = ConcurrentHashMap<String, IconCacheEntry>()

    private fun getEntry(iconPackPackageName: String?, appPackageName: String, mustBeGeneratedAfterTimeNano: Long): IconCacheEntry
    {
        val key = getIconKey(iconPackPackageName, appPackageName)

        // get existing generation request or start new generation request
        return _cache.compute(key) { _, existingEntry ->
            if (existingEntry != null && existingEntry.generationStartTimeNano > mustBeGeneratedAfterTimeNano)
            {
                // there is an existing request that started after the required time
                existingEntry
            }
            else
            {
                // no existing request or existing request started before required time
                IconCacheEntry(
                    generationStartTimeNano = System.nanoTime(),
                    iconGenerationDeferred = _scope.async {
                        generateIcon(iconPackPackageName, appPackageName, mustBeGeneratedAfterTimeNano)
                    }
                )
            }
        }!! // this never returns null
    }

    private suspend fun generateIcon(iconPackPackageName: String?, appPackageName: String, mustBeGeneratedAfterTimeNano: Long): ImageBitmap
    {
        return when (iconPackPackageName)
        {
            // default icon
            null -> _pm.getApplicationIcon(appPackageName).toBitmap().asImageBitmap()

            // icon pack icon
            else ->
            {
                val appfilter = _iconPackCache.getParsedAppFilterXML(iconPackPackageName, mustBeGeneratedAfterTimeNano)
                // TODO: obtain bitmap from icon pack

                // TODO: load actual bitmap
                val bmp = ImageBitmap(128, 128)
                return bmp
            }
        }
    }


    /**
     * Retrieves an icon for the given app from the given icon pack, or the default icon if [iconPackPackageName] is `null`.
     * @param mustBeGeneratedAfterTimeNano Last time the application has changed, needed to determine whether the current cache is still valid or not.
     */
    suspend fun getIcon(iconPackPackageName: String?, appPackageName: String, mustBeGeneratedAfterTimeNano: Long): ImageBitmap
    {
        val entry = getEntry(iconPackPackageName, appPackageName, mustBeGeneratedAfterTimeNano)
        return entry.iconGenerationDeferred.await()
    }


    private suspend fun waitForAppsToLoadAndPregenerate()
    {
        val apps = _apps.packageNameToInstalledAppMap.first { it != null }!!
        pregenerate(apps.keys)
    }

    private suspend fun pregenerate(packageNames: Iterable<String>)
    {
        // TODO: start generating bitmaps for icons that are likely to be loaded
    }

    private suspend fun onAppAddedOrChanged(packageName: String)
    {
        // TODO: consider automatically pregenerating icon if it's likely to be loaded by something soon,
        //  for example app drawer is open or project has indicated that it is using a particular icon pack
    }


    fun startup()
    {
        _scope.launch { waitForAppsToLoadAndPregenerate() }

        with(_packageEventsHolder)
        {
            _scope.launch {
                packageAddedEvents.collect { onAppAddedOrChanged(it.packageName) }
            }
            _scope.launch {
                packageReplacedEvents.collect { onAppAddedOrChanged(it.packageName) }
            }
            _scope.launch {
                packageRemovedEvents.collect {
                    // TODO: clear cached icons for a removed app (that could also be an icon pack)
                }
            }
        }
    }

    companion object
    {
        fun getIconKey(iconPackPackageName: String?, appPackageName: String) = when (iconPackPackageName)
        {
            null -> appPackageName
            else -> "$iconPackPackageName/$appPackageName"
        }
    }
}
