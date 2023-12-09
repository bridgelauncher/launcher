package com.tored.bridgelauncher.webview

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.tored.bridgelauncher.webview.serve.BridgeWebViewRequestHandler

private  const val TAG = "BridgeWebViewClient"

class BridgeWebViewClient(
    val assetLoader: BridgeWebViewRequestHandler,
) : AccompanistWebViewClient()
{
    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse?
    {
        return if (request != null)
            assetLoader.handle(request)
        else
            null
    }

    override fun onPageFinished(view: WebView, url: String?)
    {
        Log.d(TAG, "onPageFinished: $url")
        super.onPageFinished(view, url)
    }
}