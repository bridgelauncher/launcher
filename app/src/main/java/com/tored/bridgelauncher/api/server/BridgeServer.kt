package com.tored.bridgelauncher.api.server

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.tored.bridgelauncher.api.server.endpoints.AppIconsEndpoint
import com.tored.bridgelauncher.api.server.endpoints.AppsEndpoint
import com.tored.bridgelauncher.api.server.endpoints.BridgeFileServer
import com.tored.bridgelauncher.api.server.endpoints.IconPackContentEndpoint
import com.tored.bridgelauncher.api.server.endpoints.IconPacksEndpoint
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.apps.SerializableInstalledApp
import com.tored.bridgelauncher.services.iconpacks.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.settings.SettingsVM
import com.tored.bridgelauncher.utils.URLWithQueryBuilder
import com.tored.bridgelauncher.utils.q
import kotlinx.serialization.Serializable

private const val TAG = "ReqHandler"

fun getBridgeApiEndpointURL(endpoint: String, vararg queryParams: Pair<String, Any?>): String
{
    return URLWithQueryBuilder("https://${BridgeServer.HOST}/${BridgeServer.API_PATH_ROOT}/$endpoint")
        .addParams(queryParams.asIterable())
        .build()
}

@Serializable
data class BridgeAPIEndpointAppsResponse(
    val apps: List<SerializableInstalledApp>
)

class BridgeServer(
    settings: SettingsVM,
    apps: InstalledAppsHolder,
    iconPacks: InstalledIconPacksHolder,
)
{
    private val _fileServer = BridgeFileServer(settings)

    private val _endpoints = mapOf(
        ENDPOINT_APPS to AppsEndpoint(apps),
        ENDPOINT_APP_ICONS to AppIconsEndpoint(apps, iconPacks),
        ENDPOINT_ICON_PACKS to IconPacksEndpoint(iconPacks),
        ENDPOINT_ICON_PACK_CONTENT to IconPackContentEndpoint(iconPacks),
    )

    suspend fun handle(req: WebResourceRequest): WebResourceResponse?
    {
        val host = req.url.host?.lowercase()

        if (host != HOST)
            return null

        Log.i(TAG, "Request to $HOST received.")

        try
        {
            val path = req.url.path
            val apiPrefix = "/${API_PATH_ROOT}/"

            return if (path != null && path.startsWith(apiPrefix))
            {
                val endpointStr = path.substring(apiPrefix.length)
                val endpoint = _endpoints[endpointStr]

                endpoint?.handle(req)
                    ?: errorResponse(HTTPStatusCode.BadRequest, "There is no API endpoint at ${q(endpointStr)}.")
            }
            else
            {
                _fileServer.handle(req)
            }
        }
        catch (ex: HttpResponseException)
        {
            return errorResponse(ex.respStatusCode, ex.respMessage)
        }
        catch (ex: Exception)
        {
            Log.e(TAG, "Unexpected error:", ex)
            return errorResponse(HTTPStatusCode.InternalServerError, "Unexpected error: $ex")
        }
    }

    companion object
    {
        const val HOST = "bridge.launcher"
        const val PROJECT_URL = "https://$HOST/"
        const val API_PATH_ROOT = ":"

        const val ENDPOINT_ICON_PACK_CONTENT = "iconpacks/content"
        const val ENDPOINT_APPS = "apps"
        const val ENDPOINT_APP_ICONS = "appicons"
        const val ENDPOINT_ICON_PACKS = "iconpacks"
    }
}