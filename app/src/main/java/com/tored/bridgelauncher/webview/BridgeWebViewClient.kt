package com.tored.bridgelauncher.webview

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.tored.bridgelauncher.api.server.BridgeServer
import kotlinx.coroutines.runBlocking

private const val TAG = "BridgeWebViewClient"

class BridgeWebViewClient(
    val bridgeServer: BridgeServer,
) : AccompanistWebViewClient()
{
    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse?
    {
        return if (request == null)
        {
            null
        }
        else runBlocking()
        {
            bridgeServer.handle(request)
        }
    }

    override fun onPageFinished(view: WebView, url: String?)
    {
        Log.d(TAG, "onPageFinished: $url")
        super.onPageFinished(view, url)
    }
}