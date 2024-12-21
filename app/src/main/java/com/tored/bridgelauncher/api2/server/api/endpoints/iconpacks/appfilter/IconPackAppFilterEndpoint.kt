package com.tored.bridgelauncher.api2.server.api.endpoints.iconpacks.appfilter

import android.webkit.WebResourceResponse
import com.tored.bridgelauncher.api2.server.badRequest
import com.tored.bridgelauncher.api2.server.jsonResponse
import com.tored.bridgelauncher.services.iconpacks2.appfilter.parser.AppFilterXMLParsingAttemptResult
import com.tored.bridgelauncher.services.iconpacks2.appfilter.resolver.AppFilterXMLSourceOptions
import com.tored.bridgelauncher.services.iconpacks2.cache.IconPackCache
import com.tored.bridgelauncher.services.iconpacks2.list.InstalledIconPacksHolder
import com.tored.bridgelauncher.utils.q
import kotlinx.serialization.json.Json

class IconPackAppFilterEndpoint(
    private val _iconPacks: InstalledIconPacksHolder,
    private val _iconPackCache: IconPackCache,
)
{
    // /iconpacks/{packageName}/appfilter/xml
    fun getRawAppFilterXML(
        packageName: String,
        source: AppFilterXMLSourceOptions = AppFilterXMLSourceOptions.Auto,
    ): WebResourceResponse
    {

    }

    // /iconpacks/{packageName}/appfilter/parsed
    suspend fun getParsedAppFilter(packageName: String): WebResourceResponse
    {
        return when (val result = _iconPackCache.getParsedAppFilterXML(packageName, System.nanoTime()))
        {
            // TODO: better resp code than badRequest
            is AppFilterXMLParsingAttemptResult.CouldNotParseAppFilterXML -> throw badRequest("Could not parse AppFilterXML: ${q(result.reason)}")
            is AppFilterXMLParsingAttemptResult.ParsedAppFilterXML -> jsonResponse(
                Json.encodeToString(
                    AppFilterXMLParsingAttemptResult.ParsedAppFilterXML.serializer(),
                    result,
                )
            )
        }
    }
}