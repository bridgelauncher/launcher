package com.tored.bridgelauncher.api2.server.endpoints

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.tored.bridgelauncher.api2.server.BridgeAPIEndpointAppsResponse
import com.tored.bridgelauncher.api2.server.IBridgeServerEndpoint
import com.tored.bridgelauncher.api2.server.jsonResponse
import com.tored.bridgelauncher.services.apps.InstalledAppsHolder
import kotlinx.serialization.json.Json

class AppsEndpoint(private val _installedApps: InstalledAppsHolder) : IBridgeServerEndpoint
{
    override suspend fun handle(req: WebResourceRequest): WebResourceResponse
    {
        return jsonResponse(
            Json.encodeToString(
                BridgeAPIEndpointAppsResponse.serializer(),
                BridgeAPIEndpointAppsResponse(
                    apps = _installedApps.packageNameToInstalledAppMap.values.map { it.toSerializable() },
                )
            )
        )
    }
}