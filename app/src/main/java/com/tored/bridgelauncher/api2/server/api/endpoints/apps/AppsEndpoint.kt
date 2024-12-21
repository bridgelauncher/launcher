package com.tored.bridgelauncher.api2.server.api.endpoints.apps

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.tored.bridgelauncher.api2.server.jsonResponse
import com.tored.bridgelauncher.services.apps.LaunchableInstalledAppsHolder
import kotlinx.serialization.json.Json

class AppsEndpoint(private val _installedApps: LaunchableInstalledAppsHolder)
{
    suspend fun getInstalledAppsJSON(req: WebResourceRequest): WebResourceResponse
    {
        return jsonResponse(
            Json.encodeToString(
                AppsGetResp.serializer(),
                AppsGetResp(
                    apps = _installedApps.packageNameToInstalledAppMap.value?.values?.map { it.toSerializable() } ?: listOf(),
                )
            )
        )
    }
}