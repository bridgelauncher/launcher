package com.tored.bridgelauncher.services.iconpackcache

import kotlinx.coroutines.Deferred

data class IconPackCacheEntry(
    val generationStartTimeNano: Long,
    val appFilterXMLParsingDeferred: Deferred<ParsedAppFilterXML>
)
