package com.tored.bridgelauncher.ui.screens.home

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.BridgeLauncherApp
import com.tored.bridgelauncher.settings.SettingsVM
import com.tored.bridgelauncher.webview.BridgeWebChromeClient
import com.tored.bridgelauncher.webview.BridgeWebViewClient
import com.tored.bridgelauncher.webview.WebView
import com.tored.bridgelauncher.webview.WebViewState
import com.tored.bridgelauncher.webview.jsapi.JSToBridgeAPI
import com.tored.bridgelauncher.webview.serve.BridgeWebViewAssetLoader

private const val TAG = "HOMEWEBVIEW"

const val BRIDGE_PROJECT_URL = "https://bridge.project/"

@Suppress("DEPRECATION")
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HomeScreenWebView(
    webViewState: WebViewState,
    jsToBridgeAPI: JSToBridgeAPI,
    settingsVM: SettingsVM = viewModel(),
)
{
    val settingsState by settingsVM.settingsUIState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val appContext = context.applicationContext as BridgeLauncherApp

    val assetLoader = remember { BridgeWebViewAssetLoader(context, settingsState.currentProjDir) }
    val webViewClient = remember {
        BridgeWebViewClient(
            assetLoader = assetLoader
        )
    }
    val chromeClient = remember {
        BridgeWebChromeClient(
            consoleMessageCallback = {
                appContext.consoleMessagesHolder.messages.add(it)
                return@BridgeWebChromeClient true
            }
        )
    }

    // update the webview whenever the project root changes
    SideEffect()
    {
        if (assetLoader.projectRoot != settingsState.currentProjDir)
        {
            Log.d(TAG, "LaunchedEffect: assetLoader.projectRoot = ${assetLoader.projectRoot} -> ${settingsState.currentProjDir}")
            assetLoader.projectRoot = settingsState.currentProjDir
            webViewState.webView?.loadUrl(BRIDGE_PROJECT_URL)
            webViewState.webView?.reload()
        }
    }

    WebView(
        state = webViewState,
        modifier = Modifier.fillMaxSize(),
        client = webViewClient,
        chromeClient = chromeClient,
        onCreated = { webView ->

            Log.d(TAG, "HomeScreen: WebView onCreated")
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
        }
    )
}