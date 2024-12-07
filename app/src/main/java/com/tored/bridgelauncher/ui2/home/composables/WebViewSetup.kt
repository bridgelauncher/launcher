package com.tored.bridgelauncher.ui2.home.composables

import android.annotation.SuppressLint
import android.graphics.Color
import android.webkit.WebView
import com.tored.bridgelauncher.api2.jstobridge.JSToBridgeAPI
import com.tored.bridgelauncher.api2.server.BridgeServer

fun onBridgeWebViewCreated(
    webView: WebView,
    jsToBridgeAPI: JSToBridgeAPI,
)
{
    with(webView.settings)
    {
        @SuppressLint("SetJavaScriptEnabled")
        javaScriptEnabled = true
        domStorageEnabled = true
        databaseEnabled = true

        if (com.tored.bridgelauncher.utils.CurrentAndroidVersion.supportsWebViewSafeBrowsing())
            safeBrowsingEnabled = false

        // disallow file access - Bridge serves project files through a normal-looking URL, allowing projects to snoop around the user's storage would be unwise
        // TODO: give the user control over allowing this?
        allowFileAccess = false
        allowFileAccessFromFileURLs = false
        allowUniversalAccessFromFileURLs = false

        // allow access to content from Android content providers
        // TODO: give the user control over allowing this?
        allowContentAccess = true

        mediaPlaybackRequiresUserGesture = false
    }

    // clear cache to always load the project from disk
    webView.clearCache(true)

    // to be frank, I'm not sure if this actually does anything
    webView.isHapticFeedbackEnabled = true

    // make the background transparent to allow the wallpaper to be visible behind the WebView
    webView.setBackgroundColor(Color.TRANSPARENT)

    // inject the JS API object
    webView.addJavascriptInterface(jsToBridgeAPI, "Bridge")

    // immediately load the current project
    webView.loadUrl(BridgeServer.PROJECT_URL)
}