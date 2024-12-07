package com.tored.bridgelauncher.ui2.home.composables

import android.view.View
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.services.displayshape.ObserveDisplayShape
import com.tored.bridgelauncher.services.settings2.SystemBarAppearanceOptions
import com.tored.bridgelauncher.services.windowinsetsholder.ObserveWindowInsets
import com.tored.bridgelauncher.services.windowinsetsholder.WindowInsetsOptions
import com.tored.bridgelauncher.ui2.home.BridgeWebViewDeps
import com.tored.bridgelauncher.ui2.home.HomeScreen2VM
import com.tored.bridgelauncher.ui2.home.HomeScreenObserverCallbacks
import com.tored.bridgelauncher.ui2.home.HomeScreenSystemUIState
import com.tored.bridgelauncher.ui2.home.IHomeScreenProjectState
import com.tored.bridgelauncher.ui2.home.bridgemenu.BridgeMenu
import com.tored.bridgelauncher.ui2.home.bridgemenu.BridgeMenuActions
import com.tored.bridgelauncher.ui2.home.bridgemenu.BridgeMenuState
import com.tored.bridgelauncher.ui2.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.utils.ComposableContent
import com.tored.bridgelauncher.webview.WebView
import com.tored.bridgelauncher.webview.rememberSaveableWebViewState
import com.tored.bridgelauncher.webview.rememberWebViewNavigator

private val TAG = "HomeScreen2"

@Composable
fun HomeScreen2(vm: HomeScreen2VM = viewModel())
{
    HomeScreen2(
        vm.systemUIState.value,
        vm.projectState.value,
        vm.bridgeMenuState.value,
        vm.bridgeMenuActions,
        vm.webViewDeps,
        vm.observerCallbacks,
    )
}

@Composable
fun HomeScreen2(
    systemUIState: HomeScreenSystemUIState,
    projectState: IHomeScreenProjectState,
    bridgeMenuState: BridgeMenuState,
    bridgeMenuActions: BridgeMenuActions,
    // when null, show a placeholder instead of the WebView
    webViewDeps: BridgeWebViewDeps? = null,
    observerCallbacks: HomeScreenObserverCallbacks,
)
{
    val webViewState = rememberSaveableWebViewState()
    val webViewNavigator = rememberWebViewNavigator()

    SetHomeScreenSystemUIState(systemUIState)

    ObserveWindowInsets(
        options = WindowInsetsOptions.entries,
        onWindowInsetsChanged = observerCallbacks.onWindowInsetsChanged,
    )

    ObserveDisplayShape(
        observerCallbacks.onDisplayShapePathChanged,
        observerCallbacks.onCutoutPathChanged,
    )

    Box(
        modifier = Modifier
            .fillMaxSize(),
    )
    {
        when (projectState)
        {
            is IHomeScreenProjectState.FirstTimeLaunch -> PromptContainer { HomeScreenWelcomePrompt() }
            is IHomeScreenProjectState.NoStoragePerm -> PromptContainer { HomeScreenNoStoragePermsPrompt() }
            is IHomeScreenProjectState.NoProjectLoaded -> PromptContainer { HomeScreenNoProjectPrompt() }
            is IHomeScreenProjectState.Initializing -> PromptContainer { HomeScreenLoadingMessage() }
            is IHomeScreenProjectState.ProjectLoaded ->
            {
                if (webViewDeps == null)
                {
                    WebViewPlaceholder()
                }
                else
                {
                    var webView by remember { mutableStateOf<WebView?>(null) }

                    WebView(
                        state = webViewState,
                        navigator = webViewNavigator,
                        client = webViewDeps.webViewClient,
                        chromeClient = webViewDeps.chromeClient,
                        onCreated = {
                            webView = it
                            it.clearCache(true)
                            webViewDeps.onCreated(it)
                        },
                        onDispose = {
                            webView = null
                            webViewDeps.onDispose
                        },
                        modifier = Modifier.fillMaxSize(),
                    )

                    val drawOverscrollEffects = webViewDeps.drawOverscrollEffects.value
                    LaunchedEffect(drawOverscrollEffects) {
                        webView?.overScrollMode = when (drawOverscrollEffects)
                        {
                            true -> View.OVER_SCROLL_IF_CONTENT_SCROLLS
                            false -> View.OVER_SCROLL_NEVER
                        }
                    }
                }
            }
        }

        BridgeMenu(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .systemBarsPadding()
                .padding(16.dp),
            state = bridgeMenuState,
            actions = bridgeMenuActions,
        )
    }
}

@Composable
fun PromptContainer(modifier: Modifier = Modifier, content: ComposableContent)
{
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    )
    {
        content()
    }
}

@Composable
fun WebViewPlaceholder(modifier: Modifier = Modifier)
{
    PromptContainer(modifier = modifier.background(Color.LightGray))
    {
        Text(text = "WebView")
    }
}


// PREVIEWS

@Composable
@PreviewLightDark
fun HomeScreen2InitializingPreview()
{
    BridgeLauncherThemeStateless(useDarkTheme = true)
    {
        HomeScreen2(
            HomeScreenSystemUIState(
                statusBarAppearance = SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.Initializing,
            BridgeMenuState(
                isShown = false,
                isExpanded = true,
                showAppDrawerButtonWhenCollapsed = false
            ),
            BridgeMenuActions.empty(),
            observerCallbacks = HomeScreenObserverCallbacks.empty(),
        )
    }
}

@Composable
@PreviewLightDark
fun HomeScreen2NoStoragePermsPreview()
{
    BridgeLauncherThemeStateless()
    {
        HomeScreen2(
            HomeScreenSystemUIState(
                statusBarAppearance = SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.NoStoragePerm,
            BridgeMenuState(
                isShown = false,
                isExpanded = true,
                showAppDrawerButtonWhenCollapsed = false
            ),
            BridgeMenuActions.empty(),
            observerCallbacks = HomeScreenObserverCallbacks.empty(),
        )
    }
}

@Composable
@PreviewLightDark
fun HomeScreen2NoProjectPreviewNoMenu()
{
    BridgeLauncherThemeStateless()
    {
        HomeScreen2(
            HomeScreenSystemUIState(
                statusBarAppearance = SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.NoProjectLoaded,
            BridgeMenuState(
                isShown = false,
                isExpanded = false,
                showAppDrawerButtonWhenCollapsed = false,
            ),
            BridgeMenuActions.empty(),
            observerCallbacks = HomeScreenObserverCallbacks.empty(),
        )
    }
}

@Composable
@PreviewLightDark
fun HomeScreen2NoProjectPreviewMenuCollapsed()
{
    BridgeLauncherThemeStateless()
    {
        HomeScreen2(
            HomeScreenSystemUIState(
                statusBarAppearance = SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.NoProjectLoaded,
            BridgeMenuState(
                isShown = true,
                isExpanded = false,
                showAppDrawerButtonWhenCollapsed = false,
            ),
            BridgeMenuActions.empty(),
            observerCallbacks = HomeScreenObserverCallbacks.empty(),
        )
    }
}

@Composable
@PreviewLightDark
fun HomeScreen2NoProjectPreviewMenuCollapsedWithAppDrawerButton()
{
    BridgeLauncherThemeStateless()
    {
        HomeScreen2(
            HomeScreenSystemUIState(
                statusBarAppearance = SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.NoProjectLoaded,
            BridgeMenuState(
                isShown = true,
                isExpanded = false,
                showAppDrawerButtonWhenCollapsed = true,
            ),
            BridgeMenuActions.empty(),
            observerCallbacks = HomeScreenObserverCallbacks.empty(),
        )
    }
}

@Composable
@PreviewLightDark
fun HomeScreen2NoProjectPreviewMenuOpen()
{
    BridgeLauncherThemeStateless()
    {
        HomeScreen2(
            HomeScreenSystemUIState(
                statusBarAppearance = SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.NoProjectLoaded,
            BridgeMenuState(
                isShown = true,
                isExpanded = true,
                showAppDrawerButtonWhenCollapsed = false,
            ),
            BridgeMenuActions.empty(),
            observerCallbacks = HomeScreenObserverCallbacks.empty(),
        )
    }
}
