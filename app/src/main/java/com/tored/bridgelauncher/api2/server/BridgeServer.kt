package com.tored.bridgelauncher.api2.server

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.api2.server.endpoints.AppIconsEndpoint
import com.tored.bridgelauncher.api2.server.endpoints.AppsEndpoint
import com.tored.bridgelauncher.api2.server.endpoints.BridgeFileServer
import com.tored.bridgelauncher.api2.server.endpoints.IconPackContentEndpoint
import com.tored.bridgelauncher.api2.server.endpoints.IconPacksEndpoint
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import com.tored.bridgelauncher.services.apps.SerializableInstalledApp
import com.tored.bridgelauncher.services.iconpackcache.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.settings2.BridgeSetting
import com.tored.bridgelauncher.services.settings2.BridgeSettings
import com.tored.bridgelauncher.services.settings2.settingsDataStore
import com.tored.bridgelauncher.services.settings2.useBridgeSettingStateFlow
import com.tored.bridgelauncher.utils.URLWithQueryBuilder
import com.tored.bridgelauncher.utils.q
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.plus
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
    val apps: List<SerializableInstalledApp>,
)

class BridgeServer(
    private val _app: BridgeLauncherApplication,
    private val _apps: InstalledAppsHolder,
    private val _iconPacks: InstalledIconPacksHolder,
)
{
    private val _scope = CoroutineScope(Dispatchers.Main) + SupervisorJob()

    // SETTINGS
    private fun <TPreference, TResult> s(setting: BridgeSetting<TPreference, TResult>) = useBridgeSettingStateFlow(_app.settingsDataStore, _scope, setting)
    private val _currentProjDir = s(BridgeSettings.currentProjDir)

    val isReadyToServe = _currentProjDir.map { it != null }

    private val _fileServer = BridgeFileServer(
        _currentProjDir = _currentProjDir,
    )

    private val _endpoints = mapOf(
        ENDPOINT_APPS to AppsEndpoint(_apps),
        ENDPOINT_APP_ICONS to AppIconsEndpoint(_apps, _iconPacks),
        ENDPOINT_ICON_PACKS to IconPacksEndpoint(_iconPacks),
        ENDPOINT_ICON_PACK_CONTENT to IconPackContentEndpoint(_iconPacks),
    )

    suspend fun handle(req: WebResourceRequest): WebResourceResponse?
    {
        val host = req.url.host?.lowercase()

        if (host != HOST)
            return null

        Log.i(TAG, "received request to ${req.url}")

        try
        {
            val path = req.url.path
            val apiPrefix = "/$API_PATH_ROOT/"

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