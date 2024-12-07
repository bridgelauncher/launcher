package com.tored.bridgelauncher.ui2.home

import android.webkit.WebView
import androidx.compose.runtime.State
import com.tored.bridgelauncher.api2.webview.BridgeWebChromeClient
import com.tored.bridgelauncher.api2.webview.BridgeWebViewClient

data class BridgeWebViewDeps(
    val webViewClient: BridgeWebViewClient,
    val chromeClient: BridgeWebChromeClient,
    val onCreated: (webView: WebView) -> Unit,
    val onDispose: (webView: WebView) -> Unit,
    val drawOverscrollEffects: State<Boolean>,
)