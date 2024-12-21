package com.tored.bridgelauncher.api2.server.api

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.tored.bridgelauncher.api2.server.BridgeServer.Companion.API_PATH_ROOT
import com.tored.bridgelauncher.api2.server.api.endpoints.AppIconsEndpoint
import com.tored.bridgelauncher.api2.server.api.endpoints.apps.AppsEndpoint
import com.tored.bridgelauncher.api2.server.api.endpoints.iconpacks.icons.IconPackIconsEndpoint
import com.tored.bridgelauncher.api2.server.notFound
import com.tored.bridgelauncher.utils.q
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

data class QueryParam<T>(
    val name: String,
    val isRequired: Boolean,
    val parse: (raw: String) -> T,
)

class BridgeServerAPI(
    private val _apps: AppsEndpoint,
    private val _iconPacks: IconPackIconsEndpoint,
    private val _appIcons: AppIconsEndpoint,
)
{
    companion object
    {
        const val PATH_PREFIX = "/$API_PATH_ROOT/"
    }

    private fun String.normalize() = trim().lowercase()

    /**
     * Responsible for handling an incoming request, parsing it and selecting an appropriate endpoint to call.
     * Endpoints should take in typed required or optional arguments and rely on this class to parse the incoming arguments and only pass the data if it's valid.
     */
    suspend fun handle(
        req: WebResourceRequest,
    ): WebResourceResponse
    {
        val path = req.url.path
        assertNotNull(path, "Path cannot be null in a request passed to BridgeServerAPI.process().")
        assertTrue(path.startsWith(PATH_PREFIX), "Path in a request passed to BridgeServerAPI.process() must start with $PATH_PREFIX.")

        val pathSegments = path.split('/')
        val firstSegment = pathSegments.first()

        when (firstSegment.normalize())
        {
            "apps" ->
            {
                if (path.length == 1)
                    return _apps.getInstalledAppsJSON(req)
            }

            "appicons" ->
            {

            }

            "iconpacks" ->
            {

            }
        }

        // if we got here, the path doesn't match any endpoint
        throw notFound("No API endpoint for path ${q(path)}.")

        // /apps - JSON list of apps

        // /appicons/{packageName}/default - default app icon in one image
        // /appicons/{packageName}/adaptive/{part} - parts of adaptive icon + fallback
        // /appicons/{packageName} - image URL for icon from icon pack + fallback

        // /iconpacks/{packageName}/

        // /iconpacks/{packageName}/appfilter/xml
        // /iconpacks/{packageName}/appfilter/parsed
        // /iconpacks/{packageName}/appfilter/calendars
        // /iconpacks/{packageName}/appfilter/dynamicclocks

        // /iconpacks/{packageName}/iconbacks/{number}
        // /iconpacks/{packageName}/iconupon
        // /iconpacks/{packageName}/iconmask
        // /iconpacks/{packageName}/dynamicclocks/{name}/{part}
        // /iconpacks/{packageName}/drawables/{drawableName}
        // /iconpacks/{packageName}/drawables/{drawableName}/adaptive/{part}

    }
}
