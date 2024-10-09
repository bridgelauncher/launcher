package com.tored.bridgelauncher.ui.home

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.BridgeLauncherApplication
import com.tored.bridgelauncher.api.jsapi.BridgeToJSAPI
import com.tored.bridgelauncher.api.jsapi.JSToBridgeAPI
import com.tored.bridgelauncher.api.jsapi.WindowInsetsSnapshots
import com.tored.bridgelauncher.services.settings.SettingsVM
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.ui2.home.composables.HomeScreenNoProjectPrompt
import com.tored.bridgelauncher.ui2.home.composables.HomeScreenNoStoragePermsPrompt
import com.tored.bridgelauncher.utils.CurrentAndroidVersion
import com.tored.bridgelauncher.webview.WebView
import com.tored.bridgelauncher.webview.rememberSaveableWebViewState
import com.tored.bridgelauncher.webview.rememberWebViewNavigator
import kotlinx.coroutines.MainScope

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(
    hasStoragePerms: Boolean,
    settingsVM: SettingsVM = viewModel(),
)
{
    val settingsState by settingsVM.settingsState.collectAsStateWithLifecycle()
    LaunchedEffect(settingsVM) { settingsVM.request() }

    val context = LocalContext.current
    val bridge = context.applicationContext as BridgeLauncherApplication
    val webViewState = rememberSaveableWebViewState()
    val webViewNavigator = rememberWebViewNavigator()
    val jsToBridgeAPI = remember { JSToBridgeAPI(context, MainScope(), SettingsVM(bridge), null) }
//
//    SideEffect {
//        jsToBridgeAPI.settingsState = settingsState
//    }

    HomeScreenSetSystemUI(settingsState = settingsState)

//    UpdateJSAPIWindowInsets(
//        jsToBridgeAPI,
//        bridge.bridgeToJSAPI
//    )

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent)
    {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxSize(),
        )
        {
            if (settingsState.currentProjDir == null)
            {
                HomeScreenNoProjectPrompt(
                    modifier = Modifier.fillMaxSize()
                )
            }
            else if (!hasStoragePerms)
            {
                HomeScreenNoStoragePermsPrompt(
                    modifier = Modifier.fillMaxSize()
                )
            }
            else
            {
                WebView(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = webViewState,
                    navigator = webViewNavigator,
//                    client = webViewClient,
//                    chromeClient = chromeClient,
                )
            }

            if (settingsState.showBridgeButton)
            {
                Box(
                    modifier = Modifier
                        .systemBarsPadding()
                        .padding(16.dp),
                )
                {
                    var isExpanded by rememberSaveable { mutableStateOf(false) }

                    BridgeButtonStateless(
                        isExpanded,
                        onIsExpandedChange = { isExpanded = it },
                        onWebViewRefreshRequest = { webViewState.webView?.reload() },
                    )
                }
            }
        }
    }
}

@Composable
fun UpdateJSAPIWindowInsets(
    jsToBridgeAPI: JSToBridgeAPI,
    bridgeToJSAPI: BridgeToJSAPI,
)
{
    val context = LocalContext.current
    val insets = (context as Activity).window.decorView.rootWindowInsets

    val newInsets = WindowInsetsSnapshots.compose()
    jsToBridgeAPI.windowInsetsSnapshot = newInsets
    bridgeToJSAPI.windowInsetsSnapshotsChanged(newInsets)

    jsToBridgeAPI.displayShapePathSnapshot = if (CurrentAndroidVersion.supportsDisplayShape())
        insets.displayShape?.path.toString()
    else null

    jsToBridgeAPI.displayCutoutPathSnapshot = if (CurrentAndroidVersion.supportsDisplayCutout())
        insets.displayCutout?.cutoutPath.toString()
    else null
}

@Composable
@Preview(showBackground = true, backgroundColor = 0x000000)
fun HomeScreenPreview()
{
    BridgeLauncherTheme {

    }
}