package com.tored.bridgelauncher.api2.server.api.endpoints.iconpacks.info

import android.webkit.WebResourceResponse
import com.tored.bridgelauncher.api2.server.jsonResponse
import com.tored.bridgelauncher.api2.server.notFound
import com.tored.bridgelauncher.services.iconpacks2.list.InstalledIconPacksHolder
import com.tored.bridgelauncher.services.iconpacks2.list.getIconPack
import com.tored.bridgelauncher.services.iconpacks2.list.getPackageNameToIconPackMap
import com.tored.bridgelauncher.utils.q
import kotlinx.serialization.json.Json

class IconPackInfoEndpoint(
    private val _iconPacks: InstalledIconPacksHolder,
)
{
    // /iconpacks/
    suspend fun getIconPacksJSON(): WebResourceResponse
    {
        val map = _iconPacks.getPackageNameToIconPackMap()
        return jsonResponse(
            Json.encodeToString(
                IconPacksGetResp.serializer(),
                IconPacksGetResp(
                    iconPacks = map.values.map { it.toSerializable() }
                )
            )
        )
    }

    // /iconpacks/{packageName}/
    suspend fun getIconPackInfoJSON(packageName: String): WebResourceResponse
    {
        when (val pack = _iconPacks.getIconPack(packageName))
        {
            null -> throw notFound("No icon pack with package name ${q(packageName)}.")
            else -> return jsonResponse(
                Json.encodeToString(
                    IconPacksGetInfoResp.serializer(),
                    IconPacksGetInfoResp(
                        iconPack = pack.toSerializable()
                    )
                )
            )
        }
    }
}

