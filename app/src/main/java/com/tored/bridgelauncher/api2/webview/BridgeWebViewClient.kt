package com.tored.bridgelauncher.api2.webview

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.tored.bridgelauncher.api2.server.BridgeServer
import com.tored.bridgelauncher.webview.AccompanistWebViewClient
import kotlinx.coroutines.runBlocking

private const val TAG = "BridgeWebViewClient"

class BridgeWebViewClient(
    private val _bridgeServer: BridgeServer,
) : AccompanistWebViewClient()
{
    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse?
    {
        return request?.let {
            runBlocking { _bridgeServer.handle(request) }
        }
    }

    override fun onPageFinished(view: WebView, url: String?)
    {
        Log.d(TAG, "onPageFinished: $url")
        super.onPageFinished(view, url)
    }
}