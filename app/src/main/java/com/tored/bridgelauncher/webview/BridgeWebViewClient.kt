package com.tored.bridgelauncher.webview

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView

class BridgeWebViewClient(
    val assetLoader: BridgeWebViewAssetLoader,
) : AccompanistWebViewClient()
{
    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse?
    {
        return if (request != null)
            assetLoader.handle(request)
        else
            null
    }
}