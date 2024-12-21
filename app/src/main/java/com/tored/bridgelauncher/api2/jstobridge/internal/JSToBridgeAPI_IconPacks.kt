package com.tored.bridgelauncher.api2.jstobridge.internal

import android.webkit.JavascriptInterface
import com.tored.bridgelauncher.api2.server.BridgeServer
import com.tored.bridgelauncher.api2.server.api.endpoints.iconpacks.icons.IconPackIconsEndpoint
import com.tored.bridgelauncher.api2.server.getBridgeApiEndpointURL

@Suppress("ClassName")
abstract class JSToBridgeAPI_IconPacks(deps: JSToBridgeAPIDeps) : JSToBridgeAPI_Apps(deps)
{
    // BASIC

    @JavascriptInterface
    fun getIconPacksURL(): String
    {
        return getBridgeApiEndpointURL(BridgeServer.ENDPOINT_ICON_PACKS)
    }

    @JavascriptInterface
    fun getIconPackInfoURL(iconPackPackageName: String): String
    {
        return getBridgeApiEndpointURL(
            BridgeServer.ENDPOINT_ICON_PACKS,
            IconPackIconsEndpoint.QUERY_ICON_PACK_PACKAGE_NAME to iconPackPackageName,
        )
    }


    // ADVANCED

    // fetch URL for raw appfilter.xml or 404
    @JavascriptInterface
    fun getIconPackRawAppFilterXMLURL(packageName: String): String
    {
        return getBridgeApiEndpointURL(
            BridgeServer.ENDPOINT_ICON_PACKS,
            IconPackIconsEndpoint.QUERY_ICON_PACK_PACKAGE_NAME to packageName,
        )
    }

    // fetch URL for JSON appfilter.xml parsing result or 404
    @JavascriptInterface
    fun getIconPackParsedAppFilterURL(packageName: String): String
    {
    }

    // fetch URL for JSON list of calendar items from appfilter.xml
    @JavascriptInterface
    fun getIconPackCalendarsURL(packageName: String): String
    {
    }

    // fetch URL for JSON list of dynamic-clock items from appfilter.xml
    @JavascriptInterface
    fun getIconPackDynamicClocksURL(packageName: String): String
    {
    }


    // value of <scale factor="..."> from appfilter.xml
    @JavascriptInterface
    fun getIconPackScaleFactor(packageName: String): Float;

    // image URL for <iconmask img="..."> from appfilter.xml
    @JavascriptInterface
    fun getIconPackIconMaskURL(packageName: String): String
    {
    }

    // image URL for <iconupon img="..."> from appfilter.xml
    @JavascriptInterface
    fun getIconPackIconUponURL(packageName: String): String
    {
    }

    // comma-separated list of n-s found in <iconback img[n]="..."> from appfilter.xml
    @JavascriptInterface
    fun getIconPackIconBackNumbers(packageName: String): String

    // image URL for <iconback img[n]="..."> from appfilter.xml
    @JavascriptInterface
    fun getIconPackIconBackURL(n: Float): String
    {
    }

    // image URL for arbitrary drawable from icon pack or 404
    @JavascriptInterface
    fun getIconPackDrawableURL(iconPackPackageName: String, drawableName: String): String
    {
    }

    // image URL for a part of a dynamic clock drawable or 404
    @JavascriptInterface
    fun getIconPackDynamicClockDrawablePartURL(iconPackPackageName: String, dynamicClockDrawableName: String): String
    {
    }

    // image URL for a part of an adaptive icon or 404 with a configurable fallback (defaults to non-adaptive-if-background)
    @JavascriptInterface
    fun getIconPackAdaptiveIconURL(

        iconPackPackageName: String,
        adaptiveDrawableName: String,
        part: 'foreground' | 'background' | 'monochrome',
    fallbackStrategy: 'non-adaptive-if-background' | 'empty' | '404' | 'non-adaptive'
    ): String;
}