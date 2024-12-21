package com.tored.bridgelauncher.services.iconpacks2.cache

import android.util.Log
import com.tored.bridgelauncher.services.iconpacks2.appfilter.parser.AppFilterXMLParser
import com.tored.bridgelauncher.services.iconpacks2.appfilter.parser.AppFilterXMLParsingAttemptResult
import com.tored.bridgelauncher.services.iconpacks2.list.InstalledIconPacksHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.measureTimedValue

private val TAG = IconPackCache::class.simpleName

class IconPackCache(
    private val _appFilterXMLParser: AppFilterXMLParser,
    private val _installedIconPacks: InstalledIconPacksHolder,
)
{
    private val _scope = CoroutineScope(Dispatchers.Main)

    private val _cache = ConcurrentHashMap<String, IconPackCacheEntry>()

    private fun getEntry(iconPackPackageName: String, mustBeGeneratedAfterTimeNano: Long): IconPackCacheEntry
    {
        // get existing generation request or start new generation request
        return _cache.compute(iconPackPackageName) { _, existingEntry ->
            if (existingEntry != null && existingEntry.generationStartTimeNano > mustBeGeneratedAfterTimeNano)
            {
                // there is an existing request that started after the required time
                existingEntry
            }
            else
            {
                // no existing request or existing request started before required time
                IconPackCacheEntry(
                    generationStartTimeNano = System.nanoTime(),
                    appFilterXMLParsingDeferred = _scope.async {
                        parseAppFilterXML(iconPackPackageName)
                    }
                )
            }
        }!! // this never returns null
    }

    private suspend fun parseAppFilterXML(iconPackPackageName: String): AppFilterXMLParsingAttemptResult
    {
        return measureTimedValue {
            _appFilterXMLParser.attemptToParseAppFilterXML(iconPackPackageName)
        }.let { (result, dur) ->
            result.also {
                when (it)
                {
                    is AppFilterXMLParsingAttemptResult.CouldNotParseAppFilterXML ->
                    {
                        Log.w(TAG, "parseAppFilterXML($iconPackPackageName): could not parse, reason = ${it.reason}")
                    }

                    is AppFilterXMLParsingAttemptResult.ParsedAppFilterXML ->
                    {
                        Log.d(TAG, buildString {
                            appendLine("parseAppFilterXML($iconPackPackageName): OK in ${dur.inWholeMilliseconds}ms")
                            appendLine("- scaleFactor           = ${it.scaleFactor}")
                            appendLine("- iconBackImgs.count    = ${it.iconBackImgs.size}")
                            appendLine("- iconMaskImg           = ${it.iconMaskImg}")
                            appendLine("- iconUponImg           = ${it.iconUponImg}")
                            appendLine("- items.count           = ${it.items.size}")
                        })
                    }
                }
            }
        }
    }


    /**
     * Retrieves the result of loading and parsing the given icon pack's appfilter.xml file.
     * @param mustBeGeneratedAfterTimeNano Last time the icon pack application has changed, needed to determine whether the current cache is still valid or not.
     */
    suspend fun getParsedAppFilterXML(iconPackPackageName: String, mustBeGeneratedAfterTimeNano: Long): AppFilterXMLParsingAttemptResult
    {
        val entry = getEntry(iconPackPackageName, mustBeGeneratedAfterTimeNano)
        return entry.appFilterXMLParsingDeferred.await()
    }


    private suspend fun preloadIconPacksAfterInitialLoad()
    {
        val iconPacks = _installedIconPacks.packageNameToIconPackMap.first { it != null }!!
        val timeNano = System.nanoTime()

        Log.d(TAG, "preloadIconPacksAfterInitialLoad: icon pack count: ${iconPacks.size}")

        coroutineScope {
            for (packageName in iconPacks.keys)
            {
                launch {
                    getParsedAppFilterXML(iconPackPackageName = packageName, mustBeGeneratedAfterTimeNano = timeNano)
                }
            }
        }
    }

    private suspend fun preloadIconPack(packageName: String)
    {
        getParsedAppFilterXML(iconPackPackageName = packageName, mustBeGeneratedAfterTimeNano = System.nanoTime())
    }

    private fun purge(packageName: String)
    {
        _cache.remove(packageName)
    }

    fun startup()
    {
        _scope.launch { preloadIconPacksAfterInitialLoad() }
        _scope.launch { _installedIconPacks.iconPackAdded.collect { preloadIconPack(it.iconPack.packageName) } }
        _scope.launch { _installedIconPacks.iconPackChanged.collect { preloadIconPack(it.newIconPack.packageName) } }
        _scope.launch { _installedIconPacks.iconPackRemoved.collect { purge(it.oldIconPack.packageName) } }
    }
}

