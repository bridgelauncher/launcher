package com.tored.bridgelauncher.services.iconpackcache

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.plus
import java.util.concurrent.ConcurrentHashMap

class IconPackCache
{
    private val _coroutineScope = CoroutineScope(Dispatchers.Default) + SupervisorJob()

    private val _cache = ConcurrentHashMap<String, IconPackCacheEntry>()

    private  fun getEntry(iconPackPackageName: String, mustBeGeneratedAfterTimeNano: Long): IconPackCacheEntry
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
                    appFilterXMLParsingDeferred = _coroutineScope.async {
                        parseAppFilterXML(iconPackPackageName)
                    }
                )
            }
        }!! // this never returns null
    }
    
    private suspend fun parseAppFilterXML(iconPackPackageName: String): ParsedAppFilterXML
    {
        TODO()
    }


    /**
     * Retrieves the result of loading and parsing the given icon pack's appfilter.xml file.
     * @param mustBeGeneratedAfterTimeNano Last time the icon pack application has changed, needed to determine whether the current cache is still valid or not.
     */
    suspend fun getParsedAppFilterXML(iconPackPackageName: String, mustBeGeneratedAfterTimeNano: Long): ParsedAppFilterXML
    {
        val entry = getEntry(iconPackPackageName, mustBeGeneratedAfterTimeNano)
        return entry.appFilterXMLParsingDeferred.await()
    }

    fun startup()
    {

    }
}

