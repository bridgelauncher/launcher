package com.tored.bridgelauncher.ui.screens.home

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.BridgeLauncherApp
import com.tored.bridgelauncher.settings.SettingsState
import com.tored.bridgelauncher.settings.SettingsVM
import com.tored.bridgelauncher.webview.BridgeWebChromeClient
import com.tored.bridgelauncher.webview.BridgeWebViewClient
import com.tored.bridgelauncher.webview.WebView
import com.tored.bridgelauncher.webview.WebViewNavigator
import com.tored.bridgelauncher.webview.WebViewState
import com.tored.bridgelauncher.webview.jsapi.BridgeToJSAPI
import com.tored.bridgelauncher.webview.jsapi.JSToBridgeAPI
import com.tored.bridgelauncher.webview.jsapi.getBridgeButtonVisiblityString
import com.tored.bridgelauncher.webview.jsapi.getBridgeThemeString
import com.tored.bridgelauncher.webview.jsapi.getSystemBarAppearanceString
import com.tored.bridgelauncher.webview.serve.BRIDGE_PROJECT_URL
import com.tored.bridgelauncher.webview.serve.BridgeWebViewRequestHandler
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "HomeWebView"

@Suppress("DEPRECATION")
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HomeScreenWebView(
    webViewNavigator: WebViewNavigator,
    webViewState: WebViewState,
    jsToBridgeAPI: JSToBridgeAPI,
    bridgeToJSAPI: BridgeToJSAPI,
    settingsVM: SettingsVM = viewModel(),
)
{
    var settingsState by remember { mutableStateOf(SettingsState()) }

    LaunchedEffect(Unit)
    {
        settingsVM.settingsUIState.collectLatest { new ->
            val old = settingsState

            with(bridgeToJSAPI)
            {
                if (new.showBridgeButton != old.showBridgeButton)
                    bridgeButtonVisibilityChanged(getBridgeButtonVisiblityString(new.showBridgeButton))

                if (new.drawSystemWallpaperBehindWebView != old.drawSystemWallpaperBehindWebView)
                    drawSystemWallpaperBehindWebViewChanged(new.drawSystemWallpaperBehindWebView)

                if (new.theme != old.theme)
                    bridgeThemeChanged(getBridgeThemeString(new.theme))

                if (new.statusBarAppearance != old.statusBarAppearance)
                    statusBarAppearanceChanged(getSystemBarAppearanceString(new.statusBarAppearance))

                if (new.navigationBarAppearance != old.navigationBarAppearance)
                    navigationBarAppearanceChanged(getSystemBarAppearanceString(new.navigationBarAppearance))

                if (new.isDeviceAdminEnabled != old.isDeviceAdminEnabled)
                    canLockScreenChanged(new.isDeviceAdminEnabled)
            }

            settingsState = new
        }
    }

    val context = LocalContext.current
    val bridge = context.applicationContext as BridgeLauncherApp

    val assetLoader = remember { BridgeWebViewRequestHandler(context, settingsState.currentProjDir) }

    val webViewClient = remember {
        BridgeWebViewClient(
            assetLoader = assetLoader
        )
    }

    val chromeClient = remember {
        BridgeWebChromeClient(
            consoleMessageCallback = {
                bridge.consoleMessagesHolder.messages.add(it)
                return@BridgeWebChromeClient true
            }
        )
    }

    // update the webview whenever the project root changes
    SideEffect()
    {
        if (assetLoader.projectRoot != settingsState.currentProjDir)
        {
            Log.d(TAG, "assetLoader.projectRoot change: ${assetLoader.projectRoot} -> ${settingsState.currentProjDir}")
            assetLoader.projectRoot = settingsState.currentProjDir
            webViewNavigator.loadUrl(BRIDGE_PROJECT_URL)
        }
    }

    SideEffect {
        bridge.bridgeToJSAPI.webView = webViewState.webView
    }

    SideEffect {
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
        chromeClient = chromeClient,
        onCreated = { webView ->

            Log.d(TAG, "WebView onCreated")
            webView.clearCache(true)

            with(webView.settings)
            {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    safeBrowsingEnabled = false

                allowFileAccess = false
                allowFileAccessFromFileURLs = false
                allowUniversalAccessFromFileURLs = false
                allowContentAccess = true
            }

            webView.setBackgroundColor(Color.Transparent.toArgb())
            webView.addJavascriptInterface(jsToBridgeAPI, "Bridge")

            webView.loadUrl(BRIDGE_PROJECT_URL)
        }
    )
}