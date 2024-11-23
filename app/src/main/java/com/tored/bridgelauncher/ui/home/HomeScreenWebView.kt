package com.tored.bridgelauncher.ui.home

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.api.jsapi.BridgeToJSAPI
import com.tored.bridgelauncher.api.jsapi.JSToBridgeAPI
import com.tored.bridgelauncher.api.server.BridgeServer
import com.tored.bridgelauncher.services.devconsole.DevConsoleMessagesHolder
import com.tored.bridgelauncher.services.settings.SettingsState
import com.tored.bridgelauncher.utils.CurrentAndroidVersion
import com.tored.bridgelauncher.webview.BridgeWebViewClient
import com.tored.bridgelauncher.webview.WebView
import com.tored.bridgelauncher.webview.WebViewNavigator
import com.tored.bridgelauncher.webview.WebViewState

private const val TAG = "HomeWebView"

@Suppress("DEPRECATION")
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HomeScreenWebView(
    webViewNavigator: WebViewNavigator,
    webViewState: WebViewState,
//    settingsHolder: SettingsHolder = viewModel(),
    jsToBridgeAPI: JSToBridgeAPI,
    bridgeToJSAPI: BridgeToJSAPI,
    consoleMessagesHolder: DevConsoleMessagesHolder,
)
{
    val context = LocalContext.current
    val bridge = context.applicationContext as BridgeLauncherApplication

    val webViewClient = remember {
        BridgeWebViewClient(
            bridgeServer = bridge.services.bridgeServer
        )
    }

//    val chromeClient = remember {
//        BridgeWebChromeClient(
//            consoleMessageCallback = {
//
//                // cap the console message list to 2K entries to avoid expanding forever
//                if (consoleMessagesHolder.messages.size >= 2000)
//                    consoleMessagesHolder.messages.removeFirst()
//
//                consoleMessagesHolder.messages.add(it)
//
//                return@BridgeWebChromeClient true
//            }
//        )
//    }

    val settingsState = SettingsState()

    // reload the webview whenever the project dir changes
    LaunchedEffect(settingsState.currentProjDir)
    {
        webViewNavigator.loadUrl(BridgeServer.PROJECT_URL)
    }

    // update the webview in bridgeToJSAPI whenever it changes
    LaunchedEffect(webViewState.webView)
    {
        bridgeToJSAPI.webView = webViewState.webView
    }

    // update the webview whenever the drawWebViewOverscrollEffects setting changes
    LaunchedEffect(settingsState.drawWebViewOverscrollEffects)
    {
        if (settingsState.drawWebViewOverscrollEffects)
            webViewState.webView?.overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
        else
            webViewState.webView?.overScrollMode = View.OVER_SCROLL_NEVER
    }

    WebView(
        modifier = Modifier
            .fillMaxSize(),
        state = webViewState,
        navigator = webViewNavigator,
        client = webViewClient,
//        chromeClient = chromeClient,
        onCreated = { webView ->

            Log.d(TAG, "WebView onCreated")
            webView.clearCache(true)

            with(webView.settings)
            {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true

                if (CurrentAndroidVersion.supportsWebViewSafeBrowsing())
                    safeBrowsingEnabled = false

                allowFileAccess = false
                allowFileAccessFromFileURLs = false
                allowUniversalAccessFromFileURLs = false
                allowContentAccess = true

                mediaPlaybackRequiresUserGesture = false
            }

            webView.isHapticFeedbackEnabled = true

            webView.setBackgroundColor(Color.Transparent.toArgb())
            webView.addJavascriptInterface(jsToBridgeAPI, "Bridge")

            webView.loadUrl(BridgeServer.PROJECT_URL)
        }
    )
}