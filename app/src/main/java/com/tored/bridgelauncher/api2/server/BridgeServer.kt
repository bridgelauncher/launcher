package com.tored.bridgelauncher.api2.server

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.api2.server.api.BridgeServerAPI
import com.tored.bridgelauncher.api2.server.files.BridgeFileServer
import com.tored.bridgelauncher.services.apps.LaunchableInstalledAppsHolder
import com.tored.bridgelauncher.services.iconcache.IconCache
import com.tored.bridgelauncher.services.iconpacks2.list.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.settings2.BridgeSetting
import com.tored.bridgelauncher.services.settings2.BridgeSettings
import com.tored.bridgelauncher.services.settings2.settingsDataStore
import com.tored.bridgelauncher.services.settings2.useBridgeSettingStateFlow
import com.tored.bridgelauncher.utils.URLWithQueryBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

private const val TAG = "ReqHandler"

fun getBridgeApiEndpointURL(endpoint: String, vararg queryParams: Pair<String, Any?>): String
{
    return URLWithQueryBuilder("https://${BridgeServer.HOST}/${BridgeServer.API_PATH_ROOT}/$endpoint")
        .addParams(queryParams.asIterable())
        .build()
}

class BridgeServer(
    private val _app: BridgeLauncherApplication,
    private val _apps: LaunchableInstalledAppsHolder,
    private val _iconPacks: InstalledIconPacksHolder,
    private val _iconCache: IconCache,
)
{
    private val _scope = CoroutineScope(Dispatchers.Main)

    // SETTINGS
    private fun <TPreference, TResult> s(setting: BridgeSetting<TPreference, TResult>) = useBridgeSettingStateFlow(_app.settingsDataStore, _scope, setting)
    private val _currentProjDir = s(BridgeSettings.currentProjDir)

    val isReadyToServe = _currentProjDir.map { it != null }

    private val _api = BridgeServerAPI(

    )

    private val _fileServer = BridgeFileServer(
        _currentProjDir = _currentProjDir,
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

            return if (path?.startsWith(BridgeServerAPI.PATH_PREFIX) == true)
            {
                _api.handle(req)
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
    }
}