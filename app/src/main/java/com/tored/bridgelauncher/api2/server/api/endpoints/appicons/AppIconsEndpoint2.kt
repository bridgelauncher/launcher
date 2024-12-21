package com.tored.bridgelauncher.api2.server.api.endpoints.appicons

import android.webkit.WebResourceResponse

class AppIconsEndpoint2
{
    fun getDefaultIcon(packageName: String): WebResourceResponse
    {
        TODO()
    }

    fun getAdaptiveIconPart(packageName: String, fallbackStrategy: String): WebResourceResponse
    {
        TODO()
    }

    fun getIcon(packageName: String, iconPackPackageName: String, fallbackStrategy: String): WebResourceResponse
    {
        TODO()
    }
}