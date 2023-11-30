package com.tored.bridgelauncher.ui.screens.home

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.settings.SettingsVM
import com.tored.bridgelauncher.ui.theme.BridgeLauncherTheme
import com.tored.bridgelauncher.webview.jsapi.JSToBridgeAPI
import com.tored.bridgelauncher.webview.jsapi.WindowInsetsSnapshot
import com.tored.bridgelauncher.webview.rememberWebViewState

private const val TAG = "HOMESCREEN"

@Composable
fun HomeScreen(
    settingsVM: SettingsVM = viewModel(),
)
{
    val settingsState by settingsVM.settingsUIState.collectAsStateWithLifecycle()
    LaunchedEffect(settingsVM) { settingsVM.request() }

    val context = LocalContext.current
    val webViewState = rememberWebViewState(url = BRIDGE_PROJECT_URL)
    val jsToBridgeAPI = remember { JSToBridgeAPI(context, webViewState, settingsState) }

    SideEffect {
        jsToBridgeAPI.settingsState = settingsState
    }

    HomeScreenSetSystemUI(settingsState = settingsState)

    val density = LocalDensity.current

    UpdateJSAPIWindowInsets(jsToBridgeAPI)

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent)
    {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .fillMaxSize(),
        )
        {
            HomeScreenWebView(
                webViewState = webViewState,
                jsToBridgeAPI = jsToBridgeAPI,
            )

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
fun UpdateJSAPIWindowInsets(jsToBridgeAPI: JSToBridgeAPI)
{
    val context = LocalContext.current
    val insets = (context as Activity).window.decorView.rootWindowInsets

    jsToBridgeAPI.windowInsetsSnapshot = WindowInsetsSnapshot.compose()

    jsToBridgeAPI.displayShapePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) insets.displayShape?.path.toString() else null
    jsToBridgeAPI.displayCutoutPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) insets.displayCutout?.cutoutPath.toString() else null
}

@Composable
@Preview(showBackground = true, backgroundColor = 0x000000)
fun HomeScreenPreview()
{
    BridgeLauncherTheme {

    }
}