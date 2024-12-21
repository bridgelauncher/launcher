package com.tored.bridgelauncher.api2.server.api.endpoints.iconpacks.icons

import android.webkit.WebResourceResponse
import com.tored.bridgelauncher.services.iconpacks2.list.InstalledIconPacksHolder

class IconPackIconsEndpoint(
    private val _iconPacks: InstalledIconPacksHolder,
)
{
    // /iconpacks/{packageName}/iconbacks/{number}
    fun getIconBack(packageName: String, number: Number = 1): WebResourceResponse { TODO() }
    // /iconpacks/{packageName}/iconupon
    fun getIconUpon(packageName: String): WebResourceResponse { TODO() }
    // /iconpacks/{packageName}/iconmask
    fun getIconMask(packageName: String): WebResourceResponse { TODO() }
    // /iconpacks/{packageName}/dynamicclocks/{name}/{part}
    fun getDynamicClockPart(packageName: String, clockName: String, part: String): WebResourceResponse { TODO() }
    // /iconpacks/{packageName}/drawables/{drawableName}
    fun getDrawable(packageName: String, drawableName: String): WebResourceResponse { TODO() }
    // /iconpacks/{packageName}/drawables/{drawableName}/adaptive/{part}
    fun getAdaptiveIconPart(packageName: String, drawableName: String, part: String, fallbackStrategy: String): WebResourceResponse { TODO() }
}