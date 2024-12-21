package com.tored.bridgelauncher.api2.jstobridge.internal

import android.webkit.JavascriptInterface
import com.tored.bridgelauncher.api2.server.BridgeServer
import com.tored.bridgelauncher.api2.server.getBridgeApiEndpointURL
import com.tored.bridgelauncher.utils.launchApp
import com.tored.bridgelauncher.utils.openAppInfo
import com.tored.bridgelauncher.utils.requestAppUninstall

@Suppress("ClassName")
abstract class JSToBridgeAPI_Apps(deps: JSToBridgeAPIDeps) : _JSToBridgeAPI_Base(deps)
{
    @JavascriptInterface
    fun getAppsURL() = getBridgeApiEndpointURL(BridgeServer.ENDPOINT_APPS)


    @JvmOverloads
    @JavascriptInterface
    fun requestAppUninstall(packageName: String, showToastIfFailed: Boolean = true): Boolean
    {
        return tryRunInHomescreenContext(showToastIfFailed) { requestAppUninstall(packageName) }
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestOpenAppInfo(packageName: String, showToastIfFailed: Boolean = true): Boolean
    {
        return tryRunInHomescreenContext(showToastIfFailed) { openAppInfo(packageName) }
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestLaunchApp(packageName: String, showToastIfFailed: Boolean = true): Boolean
    {
        return tryRunInHomescreenContext(showToastIfFailed) { launchApp(packageName) }
    }
}