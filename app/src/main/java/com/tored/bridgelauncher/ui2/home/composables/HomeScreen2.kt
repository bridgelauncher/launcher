package com.tored.bridgelauncher.ui2.home.composables

import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tored.bridgelauncher.services.settings.SystemBarAppearanceOptions
import com.tored.bridgelauncher.ui.theme.BridgeLauncherThemeStateless
import com.tored.bridgelauncher.ui2.home.HomeScreen2VM
import com.tored.bridgelauncher.ui2.home.HomeScreenSystemUIState
import com.tored.bridgelauncher.ui2.home.IHomeScreenProjectState
import com.tored.bridgelauncher.ui2.home.bridgemenu.BridgeMenu
import com.tored.bridgelauncher.ui2.home.bridgemenu.BridgeMenuActions
import com.tored.bridgelauncher.ui2.home.bridgemenu.BridgeMenuState
import com.tored.bridgelauncher.utils.ComposableContent
import com.tored.bridgelauncher.webview.BridgeWebChromeClient
import com.tored.bridgelauncher.webview.BridgeWebViewClient
import com.tored.bridgelauncher.webview.WebView
import com.tored.bridgelauncher.webview.rememberSaveableWebViewState
import com.tored.bridgelauncher.webview.rememberWebViewNavigator

data class BridgeWebViewDeps(
    val webViewClient: BridgeWebViewClient,
    val chromeClient: BridgeWebChromeClient,
    val onCreated: (webView: WebView) -> Unit,
    val onDispose: (webView: WebView) -> Unit,
)

@Composable
fun HomeScreen2(vm: HomeScreen2VM = viewModel())
{
    val systemUIState by vm.systemUIState.collectAsStateWithLifecycle()
    val projectState by vm.projectState.collectAsStateWithLifecycle()
    val bridgeMenuState by vm.bridgeMenuState.collectAsStateWithLifecycle()
    val webViewDeps = vm.webViewDeps

    HomeScreen2(
        systemUIState,
        projectState,
        bridgeMenuState,
        vm.bridgeMenuActions,
        webViewDeps,
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
)
{
    val webViewState = rememberSaveableWebViewState()
    val webViewNavigator = rememberWebViewNavigator()

    UpdateSystemUIState(systemUIState)

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.Transparent,
    )
    {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        )
        {
            when (projectState)
            {
                is IHomeScreenProjectState.Initializing -> Unit
                is IHomeScreenProjectState.NoStoragePerm -> PromptContainer { HomeScreenNoStoragePermsPrompt() }
                is IHomeScreenProjectState.NoProjectLoaded -> PromptContainer { HomeScreenNoProjectPrompt() }
                is IHomeScreenProjectState.ProjectLoaded ->
                {
                    if (webViewDeps == null)
                    {
                        WebViewPlaceholder()
                    }
                    else
                    {
                        WebView(
                            state = webViewState,
                            navigator = webViewNavigator,
                            client = webViewDeps.webViewClient,
                            chromeClient = webViewDeps.chromeClient,
                            onCreated = webViewDeps.onCreated,
                            onDispose = webViewDeps.onDispose,
                        )
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
                statusBarAppearance =  SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.Initializing,
            BridgeMenuState(
                isShown = false,
                isExpanded = true,
                showAppDrawerButtonWhenCollapsed = false
            ),
            BridgeMenuActions.Empty(),
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
                statusBarAppearance =  SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.NoStoragePerm,
            BridgeMenuState(
                isShown = false,
                isExpanded = true,
                showAppDrawerButtonWhenCollapsed = false
            ),
            BridgeMenuActions.Empty(),
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
                statusBarAppearance =  SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.NoProjectLoaded,
            BridgeMenuState(
                isShown = false,
                isExpanded = false,
                showAppDrawerButtonWhenCollapsed = false,
            ),
            BridgeMenuActions.Empty(),
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
                statusBarAppearance =  SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.NoProjectLoaded,
            BridgeMenuState(
                isShown = true,
                isExpanded = false,
                showAppDrawerButtonWhenCollapsed = false,
            ),
            BridgeMenuActions.Empty(),
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
                statusBarAppearance =  SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.NoProjectLoaded,
            BridgeMenuState(
                isShown = true,
                isExpanded = false,
                showAppDrawerButtonWhenCollapsed = true,
            ),
            BridgeMenuActions.Empty(),
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
                statusBarAppearance =  SystemBarAppearanceOptions.Hide,
                navigationBarAppearance = SystemBarAppearanceOptions.Hide,
                drawSystemWallpaperBehindWebView = false,
            ),
            IHomeScreenProjectState.NoProjectLoaded,
            BridgeMenuState(
                isShown = true,
                isExpanded = true,
                showAppDrawerButtonWhenCollapsed = false,
            ),
            BridgeMenuActions.Empty(),
        )
    }
}
