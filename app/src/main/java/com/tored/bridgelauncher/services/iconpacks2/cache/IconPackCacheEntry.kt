package com.tored.bridgelauncher.services.iconpacks2.cache

import com.tored.bridgelauncher.services.iconpacks2.appfilter.parser.AppFilterXMLParsingAttemptResult
import kotlinx.coroutines.Deferred

data class IconPackCacheEntry(
    val generationStartTimeNano: Long,
    val appFilterXMLParsingDeferred: Deferred<AppFilterXMLParsingAttemptResult>
)
