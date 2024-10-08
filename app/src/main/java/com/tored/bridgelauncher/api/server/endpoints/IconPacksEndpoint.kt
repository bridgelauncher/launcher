package com.tored.bridgelauncher.api.server.endpoints

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.tored.bridgelauncher.api.server.IBridgeServerEndpoint
import com.tored.bridgelauncher.services.iconpacks.InstalledIconPacksHolder

class IconPacksEndpoint(
    private val _iconPacks: InstalledIconPacksHolder
) : IBridgeServerEndpoint
{
    override suspend fun handle(req: WebResourceRequest): WebResourceResponse
    {
        TODO("Not yet implemented")
    }

    companion object
    {
        const val QUERY_INCLUDE_ITEMS = "includeItems"
        const val QUERY_ICON_PACK_PACKAGE_NAME = "iconPackPackageName"
    }
}